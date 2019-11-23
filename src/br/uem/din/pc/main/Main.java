/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uem.din.pc.main;

import br.uem.din.pc.controller.ArquivoController;
import br.uem.din.pc.controller.PontosController;
import br.uem.din.pc.model.PontosModel;
import br.uem.din.pc.view.ConsoleView;
import java.util.concurrent.CyclicBarrier;

/**
 *
 * @author Luiz Flávio
 */
public class Main {

    public static int qtdeThreads = 1;
    public static boolean houverAlteracao = true;
    public static PontosModel dadosArqBase = new PontosModel();
    public static PontosModel dadosArqCentroid = new PontosModel();
    public static CyclicBarrier barreiraThread = new CyclicBarrier(qtdeThreads);
    public static long tempoInicial = 0;
    public static long tempoFinal = 0;

    public static void main(String[] args) throws Exception {
        int opcao = ConsoleView.showMenu();
        while (opcao != 0) {
            ConsoleView.limparConsole();
            switch (opcao) {
                case 1:
                    dadosArqBase.setPontos(ArquivoController.iniciarlizarDados(1, dadosArqCentroid));
                    break;
                case 2:
                    dadosArqCentroid.setPontos(ArquivoController.iniciarlizarDados(0, dadosArqBase));
                    break;
                case 3:
                    PontosController.imprimirPontos(dadosArqBase);
                    break;
                case 4:
                    PontosController.imprimirPontos(dadosArqCentroid);
                    break;
                case 5:
                    dadosArqBase = new PontosModel();
                    dadosArqCentroid = new PontosModel();
                    System.out.println("As informações foram apagadas com sucesso!");
                    break;
                case 6:
                    PontosController.calculaKMeansSequencial(dadosArqBase, dadosArqCentroid);
                    break;
                case 7:
                    PontosController.calculaKMeansParalelo(dadosArqBase, dadosArqCentroid, qtdeThreads, barreiraThread);
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
            System.gc();
            opcao = ConsoleView.showMenu();
        }
    }
}
