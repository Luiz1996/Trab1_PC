/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uem.din.pc.controller;

import br.uem.din.pc.main.Main;
import static br.uem.din.pc.main.Main.houverAlteracao;
import java.util.List;
import br.uem.din.pc.model.PontosModel;
import br.uem.din.pc.model.CoordenadasModel;
import br.uem.din.pc.view.ConsoleView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;

/**
 *
 * @author Luiz Flávio
 */
public class PontosController {
    
    public static Scanner in = new Scanner(System.in);

    public static void calculaKMeansSequencial(PontosModel dadosBase, PontosModel dadosCentroit) throws Exception {
        if (!dadosBase.getPontos().isEmpty() && !dadosCentroit.getPontos().isEmpty()) {
            houverAlteracao = true;
            System.out.println("Os dados estão sendo processados, aguarde um momento...\n");

            Main.tempoInicial = System.currentTimeMillis();
            int qtdePontosBase = dadosBase.getPontos().size();
            int qtdePontosCentroide = dadosCentroit.getPontos().size();
            int qtdeCoordenadas = dadosCentroit.getPontos().get(0).getCoordenada().size();

            while (houverAlteracao) {
                atualizarVinculoPontoECentroide(dadosBase, dadosCentroit, qtdePontosBase, qtdePontosCentroide);
                houverAlteracao = atualizarPosicaoCentroide(dadosBase, dadosCentroit, qtdePontosBase, qtdePontosCentroide, qtdeCoordenadas);
            }

            Main.tempoFinal = System.currentTimeMillis();
            
            System.out.println("A execução durou: " + ((Main.tempoFinal - Main.tempoInicial) / 1000) + " segundo(s).\n");
            ArquivoController.escreverArquivoCentroideAtualizado(dadosCentroit);
        } else {
            System.out.println("Erro ao processar, é necessário realizar a importação do(s) arquivo(s) de base(s) e/ou centróide(s).");
        }
    }
    
    public static void calculaKMeansParalelo(PontosModel dadosArqBase, PontosModel dadosArqCentroid, int qtdeThreads, CyclicBarrier barreiraThread) throws InterruptedException, IOException {
        if (!dadosArqBase.getPontos().isEmpty() && !dadosArqCentroid.getPontos().isEmpty()) {
            in = new Scanner(System.in);
            houverAlteracao = true;
            System.out.print("Quantas Threads deseja utilizar?\nR: ");
            
            try{
                qtdeThreads = in.nextInt();
            }catch(InputMismatchException e){
                qtdeThreads = 0;
            }
            
            //limpa console
            ConsoleView.limparConsole();
            
            if(qtdeThreads <= 0){
                System.out.println("A quantidade de Threads informada é inválida, processamento abortado!");
                return;
            }

            //declarando vetor de Threads
            Thread minhasThreads[] = new Thread[qtdeThreads];
            
            //atualizando barreira
            barreiraThread = new CyclicBarrier(qtdeThreads);

            //long tempoInicial = System.currentTimeMillis();
            System.out.println("Os dados estão sendo processados, aguarde um momento...\n");

            //setando Runnable a ser executado pela Thread e seu 'Id'
            for (int i = 0; i < minhasThreads.length; i++) {
                minhasThreads[i] = new Thread(new PontosControllerThread(dadosArqBase, dadosArqCentroid, barreiraThread, qtdeThreads));
                minhasThreads[i].setName(String.valueOf(i));
            }

            //obtendo tempo inicial do processamento
            Main.tempoInicial = System.currentTimeMillis();

            //iniciando execução de Threads
            for (Thread minhasThread : minhasThreads) {
                minhasThread.start();
            }

            //obrigando a Thread Main aguardar execução de todas as demais Threads
            for (Thread minhasThread : minhasThreads) {
                minhasThread.join();
            }

            //obtendo tempo final de processamento e apresentando em tela
            Main.tempoFinal = System.currentTimeMillis();
            System.out.println("A execução durou: " + ((Main.tempoFinal - Main.tempoInicial) / 1000) + " segundo(s).\n");

            //escrevendo resultado em arquivo
            ArquivoController.escreverArquivoCentroideAtualizado(dadosArqCentroid);
        } else {
            System.out.println("Erro ao processar, é necessário realizar a importação do(s) arquivo(s) de base(s) e/ou centróide(s).");
        }
    }

    public static void atualizarVinculoPontoECentroide(PontosModel dadosBase, PontosModel dadosCentroit, int qtdePontosBase, int qtdePontosCentroide) throws Exception {
        for (int i = 0; i < qtdePontosBase; i++) {
            for (int j = 0; j < qtdePontosCentroide; j++) {
                double vlrDistanciaPontoBase_PontoCentroide = Math.floor(calcularDistanciaEntrePontoBase_PontoCentroide(dadosBase.getPontos().get(i), dadosCentroit.getPontos().get(j)));
                if (vlrDistanciaPontoBase_PontoCentroide < dadosBase.getPontos().get(i).getDistanciaCentroid()) {
                    
                    //se identificado uma distancia menor, então é atualizado o vinculo entre ponto base x ponto centróide
                    dadosBase.getPontos().get(i).setDistanciaCentroid(vlrDistanciaPontoBase_PontoCentroide);
                    dadosBase.getPontos().get(i).setIdCentroide((j + 1));
                }
            }
        }
    }

    public static double calcularDistanciaEntrePontoBase_PontoCentroide(CoordenadasModel pontoBase, CoordenadasModel pontoCentroide) throws Exception {
        double totalSum = 0;
        int qtdeCoordenadas = pontoBase.getCoordenada().size();

        for (int i = 0; i < qtdeCoordenadas; i++) {
            totalSum += Math.pow((pontoBase.getCoordenada().get(i) - pontoCentroide.getCoordenada().get(i)), 2);
        }

        return Math.sqrt(totalSum);
    }

    public static boolean atualizarPosicaoCentroide(PontosModel dadosBase, PontosModel dadosCentroit, int qtdePontosBase, int qtdePontosCentroide, int qtdeCoordenadas) {
        boolean houveAlteracao = false;

        for (int i = 0; i < qtdePontosCentroide; i++) {
            //inicializando lista auxiliar
            List<Integer> coordAcumulada = new ArrayList<>();
            for (int z = 0; z < qtdeCoordenadas; z++) {
                coordAcumulada.add(0);
            }

            int qtdePontos = 0;
            //percorre todos os pontos encontrando os vinculos, se identificar é somado as coordenadas(abaixo elas serão divididas pela quantidade de pontos encontrados)
            for (int j = 0; j < qtdePontosBase; j++) {
                if ((i + 1) == dadosBase.getPontos().get(j).getIdCentroide()) {
                    qtdePontos++;
                    for (int k = 0; k < qtdeCoordenadas; k++) {
                        coordAcumulada.set(k, (coordAcumulada.get(k) + dadosBase.getPontos().get(j).getCoordenada().get(k)));
                    }
                    dadosBase.getPontos().get(j).setDistanciaCentroid(Double.MAX_VALUE);
                }
            }

            if (qtdePontos > 0) {
                //realiza o calculo das novas médias, esse dado fica salvo no coordenadaAcumulada
                for (int j = 0; j < qtdeCoordenadas; j++) {
                    try {
                        coordAcumulada.set(j, Math.floorDiv(coordAcumulada.get(j), qtdePontos));
                    } catch (ArithmeticException e) {
                        coordAcumulada.set(j, 0);
                    }
                }

                //resetando valor de variável contadora
                qtdePontos = 0;

                //compara coordenada x coordenadaAcumulada
                if (!coordAcumulada.equals(dadosCentroit.getPontos().get(i).getCoordenada())) {
                    dadosCentroit.getPontos().get(i).setCoordenada(coordAcumulada);
                    houveAlteracao = true;
                }
            }
        }
        return houveAlteracao;
    }
    
    public static void imprimirPontos(PontosModel pontos) {
        if (pontos.getPontos().isEmpty()) {
            System.out.println("Nenhuma informação a ser impressa...");
        } else {
            for (int i = 0; i < pontos.getPontos().size(); i++) {
                System.out.print("Ponto " + (i + 1));

                if (pontos.getPontos().get(i).getIdCentroide() != -1) {
                    System.out.println(" -> [Centróide Vinculado: " + pontos.getPontos().get(i).getIdCentroide() + ", Distância: " + pontos.getPontos().get(i).getDistanciaCentroid() + "]");
                } else {
                    System.out.println("");
                }

                System.out.println(pontos.getPontos().get(i).getCoordenada().toString() + "\n");
            }
        }
    }
}
