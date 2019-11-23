/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uem.din.pc.controller;

import static br.uem.din.pc.main.Main.houverAlteracao;
import java.util.List;
import br.uem.din.pc.model.PontosModel;
import br.uem.din.pc.model.CoordenadasModel;
import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Luiz Flávio
 */
public class PontosControllerThread implements Runnable{
    PontosModel pontosBase;
    PontosModel pontosCentroides;
    CyclicBarrier barreira;
    int qtdeThreads;
    
    public PontosControllerThread(PontosModel pontsBase, PontosModel pontosCentroides, CyclicBarrier barreira, int qtdeThreads){
        this.pontosBase = pontsBase;
        this.pontosCentroides = pontosCentroides;
        this.barreira = barreira;
        this.qtdeThreads = qtdeThreads;
    }
    
    public void atualizarVinculoPontoECentroide(PontosModel dadosBase, PontosModel dadosCentroit, int qtdePontosBase, int qtdePontosCentroide) throws Exception {
        int idThread = Integer.parseInt(Thread.currentThread().getName());
        for (int i = idThread; i < qtdePontosBase; i += qtdeThreads) {
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

    public double calcularDistanciaEntrePontoBase_PontoCentroide(CoordenadasModel pontoBase, CoordenadasModel pontoCentroide) throws Exception {
        double totalSum = 0;
        int qtdeCoordenadas = pontoBase.getCoordenada().size();

        for (int i = 0; i < qtdeCoordenadas; i++) {
            totalSum += Math.pow((pontoBase.getCoordenada().get(i) - pontoCentroide.getCoordenada().get(i)), 2);
        }

        return Math.sqrt(totalSum);
    }

    public void atualizarPosicaoCentroide(PontosModel dadosBase, PontosModel dadosCentroit, int qtdePontosBase, int qtdePontosCentroide, int qtdeCoordenadas) {
        List<Integer> coordAcumulada = new ArrayList<>();

        int idThread = Integer.parseInt(Thread.currentThread().getName());
        for (int i = idThread; i < qtdePontosCentroide; i+= qtdeThreads) {
            //inicializando lista auxiliar
            
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

                //compara coordenada x coordenadaAcumulada, se forem diferentes então o centroide é reposicionado
                if (!coordAcumulada.equals(dadosCentroit.getPontos().get(i).getCoordenada())) {
                    dadosCentroit.getPontos().get(i).setCoordenada(coordAcumulada);
                    houverAlteracao = true;
                }
            }
            coordAcumulada = new ArrayList<>();
        }
    }

    @Override
    public void run() {
        int qtdePontosBase = pontosBase.getPontos().size();
        int qtdePontosCentroide = pontosCentroides.getPontos().size();
        int qtdeCoordenadas = pontosCentroides.getPontos().get(0).getCoordenada().size();

        while (houverAlteracao) {  
            //barreira
            try {
                barreira.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                Logger.getLogger(PontosControllerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            houverAlteracao = false;
            
            //recalcula as distâncias entre os pontos da base e pontos dos centróides
            try {
                atualizarVinculoPontoECentroide(pontosBase, pontosCentroides, qtdePontosBase, qtdePontosCentroide);
            } catch (Exception ex) {
                System.out.println("Erro ao calcular distâncias entre pontos da base e pontos do centróides.\n\nErro: ".concat(ex.getMessage().trim()).concat("\n\nPrograma abortado!"));
                System.exit(0);
            }
            
            //barreira
            try {
                barreira.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                Logger.getLogger(PontosControllerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            atualizarPosicaoCentroide(pontosBase, pontosCentroides, qtdePontosBase, qtdePontosCentroide, qtdeCoordenadas);              
            
            //barreira
            try {
                barreira.await();
            } catch (InterruptedException | BrokenBarrierException ex) {
                Logger.getLogger(PontosControllerThread.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
    }
}
