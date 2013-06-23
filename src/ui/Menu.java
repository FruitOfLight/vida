package ui;

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

import javax.swing.JFileChooser;

import enums.RunState;

public class Menu {

    static String[] menuItems;
    static String[][] allMenuItems;

    public static void init() {
        menuItems = TextBook.getLanguageArray("menuItems");
        String[] help = TextBook.getLanguageArray("allMenuItems");
        allMenuItems = new String[help.length][];
        for (int i = 0; i < help.length; i++)
            allMenuItems[i] = help[i].split("##");
    }

    static void performAction(int r, int c) {
        switch (r) {
        case 0:
            switch (c) {
            case 0:
                Dialog.showMessage("Nothing to do");
                break;
            case 1:
                GUI.saveApp();
                System.exit(0);
                return;
            default:
                System.out.println("Invalid entry!");
            }
            break;

        case 1:
            switch (c) {
            case 0:
                GUI.player.graph.createNew();
                break;
            case 1:
                int value = GUI.graphLoader.showOpenDialog(null);
                if (value == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = GUI.graphLoader.getSelectedFile();
                        Scanner input = new Scanner(file);
                        GUI.player.graph.read(input);
                        input.close();
                    } catch (Exception e) {
                        System.out.println("Exception during opening\n");
                    }
                    GUI.gRepaint();
                }
                break;
            case 2:
                value = GUI.graphSaver.showSaveDialog(null);
                if (value == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = GUI.graphSaver.getSelectedFile();
                        PrintStream output = new PrintStream(file);
                        GUI.player.graph.print(output);
                        output.close();
                    } catch (Exception e) {
                        System.out.println("Exception during saving\n");
                    }
                }
                break;
            default:
                System.out.println("Invalid entry!");
            }
            break;
        case 2:
            if (GUI.player.state != RunState.stopped)
                break;
            switch (c) {
            case 0:
                GUI.player.model.openAlgorithm();
                break;
            case 1:
                GUI.player.model.settings.showDialog();
                break;
            default:
                Dialog.showError("Not implemented");
            }
            break;
        case 3:
            /*if (GUI.player.state != RunState.stopped)
                break;
            switch (c) {
            case 0:
                //GUI.player.model.algorithm = null;
                break;
            case 1:
                //GUI.player.model.algorithm = new CliqueLEAlgorithm();
                break;
            case 2:
                //GUI.player.model.algorithm = new BFSAlgorithm();
                break;
            default:*/
            Dialog.showError("Not implemented");
            //}
            break;
        default:
            System.out.println("Invalid entry!");
        }
    }
}
