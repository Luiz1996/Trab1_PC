/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.uem.din.pc.view;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author luiz.pereira
 */
public class ConsoleView {
    public final static void limparConsole() {
        try {
            Robot robot = new Robot();
            robot.setAutoDelay(10);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_L);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_L);
        } catch (AWTException ex) {
            JOptionPane.showMessageDialog(null, "Falha ao limpar console!\nErro: ".concat(ex.getMessage().trim()), "Falha ao limpar console", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static int showMenu() {
        Scanner in = new Scanner(System.in);    
        System.out.println("-------------- Menu do Sistema K-Means --------------");
        System.out.println("|    01) Ler arquivo base                           |");
        System.out.println("|    02) Ler arquivo centroid                       |");
        System.out.println("|    03) Imprimir dados base                        |");
        System.out.println("|    04) Imprimir dados centroid                    |");
        System.out.println("|    05) Resetar informacoes lidas                  |");
        System.out.println("|    06) K-means sequencial                         |");
        System.out.println("|    07) K-means paralelo                           |");
        System.out.println("|    00) Sair                                       |");
        System.out.println("-----------------------------------------------------");
        System.out.print("Opcao: "); 
        
        try{
            return in.nextInt();
        }catch(InputMismatchException e){
            return -1;
        }
    }
}
