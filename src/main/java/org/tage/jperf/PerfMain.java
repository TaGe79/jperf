package org.tage.jperf;

import org.openjdk.jol.info.ClassLayout;
import org.openjdk.jol.info.GraphLayout;
import org.openjdk.jol.vm.VM;

import java.io.IOException;

/**
 *
 */
//public class org.tage.jperf.StringVsClass extends SimpleBenchmark {
public class PerfMain {

  public static void main(String[] args) throws IOException {
    stringVsCharArray();
//    new Runner().run(
//      // These are the command line arguments for Runner. You can add
//      // "--trials", "10" to run each benchmark 10 times each.
//      org.tage.jperf.PerfMain.class.getName()
//    );

  }



  private static void stringVsCharArray() {
    System.out.println(VM.current().details());

    final String mic = new String("UMPALUMPA");
    final String mic2 = new String("UMPALUMPA");
    final char[] mica = new char[]{'U', 'M', 'P', 'A','L','U','M','P','A'};
    final char[] b = new char[]{};

    System.out.println("mic -------------------------------------------");
    System.out.println(ClassLayout.parseInstance(mic).toPrintable());
    System.out.println(GraphLayout.parseInstance(mic).toPrintable());
    System.out.println("mic2 -------------------------------------------");
    System.out.println(GraphLayout.parseInstance(mic2).toPrintable());

    System.out.println("mica -------------------------------------------");

    System.out.println(ClassLayout.parseInstance(mica).toPrintable());
    System.out.println(GraphLayout.parseInstance(mica).toPrintable());

    System.out.println("b -------------------------------------------");
    System.out.println(GraphLayout.parseInstance(b).toPrintable());
  }
}
