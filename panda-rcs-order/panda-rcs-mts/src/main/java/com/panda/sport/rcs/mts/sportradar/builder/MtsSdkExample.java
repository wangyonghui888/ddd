/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.panda.sport.rcs.mts.sportradar.builder;

import com.panda.sport.rcs.mts.sportradar.order.*;

import java.io.IOException;

public class MtsSdkExample {

    public static void main(String[] args) throws IOException {

        char key = 'y';
        while (key == 'y')
        {
            DoExampleSelection();

            System.out.println();
            System.out.println("Want to run another example? (y|n): ");
            key = (char) System.in.read();
            System.in.read();
            System.out.println();
        }
    }

    private static void DoExampleSelection() throws IOException {
        System.out.println();
        System.out.println(" Select which example you want to run:");
        System.out.println(" 1 - Basic \t\t\t\t(normal sending ticket and receiving response via event handler)");
        System.out.println(" 2 - Blocking \t\t\t(sending ticket and receiving response with blocking mode)");
        System.out.println(" 3 - Reoffer \t\t\t(sending reoffer to the declined ticket and receiving response)");
        System.out.println(" 4 - Cashout \t\t\t(sending a cashout for an accepted ticket)");
        System.out.println(" 5 - Examples \t\t\t(creating and sending ticket examples from MTS integration guide)");
        System.out.print(" Enter number: ");

        char k = (char) System.in.read();
        System.in.read();

        System.out.println();
        System.out.println();

        switch (k)
        {
            case '1':
            {
                Basic basic =  new Basic();
                basic.Run();
                break;
            }
            case '2':
            {
                //Blocking.Run();
                break;
            }
            case '3':
            {
                //Reoffer.Run();
               // break;
            }
            case '4':
            {
               //Cashout.Run();
                //break;
            }
            case '5':
            {
                //Examples.Run();
                //break;
            }
            default:
            {
                DoExampleSelection();
                break;
            }
        }
    }
}
