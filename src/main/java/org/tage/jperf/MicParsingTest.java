package org.tage.jperf;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 */
public class MicParsingTest extends SimpleBenchmark {
  public static int pack(final byte[] value) {
    int result = 0;
    switch (value.length) {
      case 4:
        result |= (value[3]);
      case 3:
        result |= ((int) value[2] << 8);
      case 2:
        result |= ((int) value[1] << 16);
      case 1:
        result |= ((int) value[0] << 24);
        break;

      default:
        throw new IllegalArgumentException("Invalid array size:" + new String(value));
    }

    return result;
  }

  public static byte[] unpack(final int value) {
    final int length = (value & 0x000000ff) != 0 ? 4 :
      (value & 0x0000ff00) != 0 ? 3 :
        (value & 0x00ff0000) != 0 ? 2 :
          (value & 0xff000000) != 0 ? 1 : 0;

    final byte[] result = new byte[length];
    if (length > 0) {
      result[0] = (byte) ((value & 0xff000000) >> 24);
    }
    if (length > 1) {
      result[1] = (byte) ((value & 0x00ff0000) >> 16);
    }
    if (length > 2) {
      result[2] = (byte) ((value & 0x0000ff00) >> 8);
    }
    if (length > 3) {
      result[3] = (byte) (value & 0x000000ff);
    }

    return result;
  }

  public static void main(String[] args) throws Exception {
    if (args == null || args.length == 0) {
      System.out.println("USAGE:");
      System.out.println("\tcaliper benchmark: java -jar MicParsingTest.jar caliper");
      System.out.println("\tmemory usage: java -jar MicParsingTest.jar [1,2,3]");
      return;
    }

    if (args[0].equals("caliper")) {
      caliperPerformanceTest();
    } else {
      memoryUsage(Integer.parseInt(args[0]));
    }
  }

  static void caliperPerformanceTest() {
    new Runner()
      .run(
        // These are the command line arguments for Runner. You can add
        // "--trials", "10" to run each benchmark 10 times each.
        org.tage.jperf.MicParsingTest.class.getName()
      );
  }

  static void memoryUsage(int variant) throws Exception {
    final Map<String, List<String>> result;
    switch (variant) {
      case 1:
        System.out.println("---------------- timeParseMicFileApachePLow --------------------");
      {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.println(String.format("Used Memory before: %,d", usedMemoryBefore));
        Object[] ores = MicParsingTest.parseMicFileApachePLow();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println(String.format("Memory increased: %,d", (usedMemoryAfter - usedMemoryBefore)));
        result = new HashMap<>();
        for (int i = 0; i < ((String[]) ores[1]).length && ((String[]) ores[1])[i] != null; i++) {
          final String key = new String(unpack(((int[]) ores[0])[i]));
          final String[] mics = ((String[]) ores[1])[i].split(",");
          result.put(key, Arrays.stream(mics).collect(Collectors.toList()));
        }
      }
      break;
      case 2:
        System.out.println("----------------- timeParseMicFileApacheLow ----------------");
      {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.println(String.format("Used Memory before: %,d", usedMemoryBefore));
        result = MicParsingTest.parseMicFileApacheLow();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println(String.format("Memory increased: %,d", (usedMemoryAfter - usedMemoryBefore)));
      }
      break;
      case 3:
        System.out.println("---------------- timeParseMicFileApacheStream -----------------");

      {
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.println(String.format("Used Memory before: %,d", usedMemoryBefore));
        result = MicParsingTest.parseMicFileApacheStream();
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println(String.format("Memory increased: %,d", (usedMemoryAfter - usedMemoryBefore)));
      }
      break;
      default:
        result = Collections.emptyMap();
    }
    System.out.println();
    System.out.println();

    System.out.println(result.entrySet().stream()
      .map(e -> e.getKey() + " => " + String.join(", ", e.getValue()))
      .collect(Collectors.joining("\n")));
  }

  public void timeParseMicFileApachePLow(int perf) throws Exception {
    for (int i = 0; i < perf; i++) {
      parseMicFileApachePLow();
    }
  }

  public void timeParseMicFileApacheLow(int perf) throws Exception {
    for (int i = 0; i < perf; i++) {
      parseMicFileApacheLow();
    }
  }

  public void timeParseMicFileApacheStream(int perf) throws Exception {
    for (int i = 0; i < perf; i++) {
      parseMicFileApacheStream();
    }
  }

  static Map<String, List<String>> parseMicFileApacheStream() throws IOException {
    final CSVParser parser = CSVParser
      .parse(MicParsingTest.class.getClassLoader()
        .getResourceAsStream("ISO10383_MIC.csv"), StandardCharsets.UTF_8, CSVFormat.DEFAULT
        .withHeader("COUNTRY", "ISO COUNTRY CODE (ISO 3166)", "MIC", "OPERATING MIC", "O/S", "NAME-INSTITUTION DESCRIPTION", "ACRONYM", "CITY", "WEBSITE", "STATUS DATE", "STATUS", "CREATION DATE", "COMMENTS"));

    final List<CSVRecord> records = parser.getRecords();
    return records.stream().skip(1)
      .collect(Collectors.groupingBy(r -> r.get(1),
        Collectors.mapping(r -> r.get(3), Collectors.toList())));
  }

  static Map<String, List<String>> parseMicFileApacheLow() throws IOException {
    final CSVParser parser = CSVParser
      .parse(MicParsingTest.class.getClassLoader()
        .getResourceAsStream("ISO10383_MIC.csv"), StandardCharsets.UTF_8, CSVFormat.DEFAULT
        .withHeader("COUNTRY", "ISO COUNTRY CODE (ISO 3166)", "MIC", "OPERATING MIC", "O/S", "NAME-INSTITUTION DESCRIPTION", "ACRONYM", "CITY", "WEBSITE", "STATUS DATE", "STATUS", "CREATION DATE", "COMMENTS"));
    final List<CSVRecord> records = parser.getRecords();
    final Map<String, List<String>> countryMic = new HashMap<>();
    for (int i = 1; i < records.size(); i++) {
      final CSVRecord record = records.get(i);
      final List<String> codes = countryMic.get(record.get(1));
      if (codes == null) {
        final ArrayList<String> value = new ArrayList<>();
        value.add(record.get(2));
        countryMic.put(record.get(1), value);
      } else {
        codes.add(record.get(2));
      }
    }
    return countryMic;
  }

  static Object[] parseMicFileApachePLow() throws Exception {
    final char newline = '\n';
    final char comma = ',';

    char[] cbuf = new char[100];
    byte[] ba = new byte[4];
    char[] devNull = new char[8];
    int i = 0;
    int commaIdx, countryOffset, micOffset;
    String[] sres = new String[195];
    int[] cmap = new int[195];
    int cidx = 0, cfound = 0;
    int countryIso;

    try (final BufferedReader br = new BufferedReader(
      new InputStreamReader(MicParsingTest.class.getClassLoader()
        .getResourceAsStream("ISO10383_MIC.csv")))) {
      //skip 1st line
      for (; i != -1 && devNull[0] != newline; i = br.read(devNull)) {
        ;
      }
      devNull[0] = 0;

      do {
        i = br.read(cbuf);
        if (i == -1) {
          break;
        }

        // determine offsets
        for (commaIdx = 0; cbuf[commaIdx] != comma; commaIdx++) {
          ;
        }
        countryOffset = ++commaIdx;
        for (; cbuf[commaIdx] != comma; commaIdx++) {
          ;
        }
        micOffset = ++commaIdx;

        // encode countryIso
        for (i = 0; i < 4; i++) {
          if (countryOffset + i < micOffset - 1) {
            ba[i] = (byte) cbuf[countryOffset + i];
          } else {
            ba[i] = 0;
          }
        }
        countryIso = pack(ba);

        for (; cbuf[commaIdx] != comma; commaIdx++) {
          ;
        }

        // map countryIso to index position
        if (cidx == 0) {
          cmap[cidx++] = countryIso;
        } else {
          cfound = find(cmap, cidx, countryIso);
          if (cfound == -1) {
            cfound = cidx++;
            cmap[cidx] = countryIso;
          }
        }
        final String mic = String.valueOf(cbuf, micOffset, (commaIdx - micOffset));

        // read until newline
        for (i = 0; i != -1 && devNull[0] != newline; i = br.read(devNull)) {
          ;
        }
        devNull[0] = 0;

        // add mic to given country
        sres[cfound] = sres[cfound] == null ? mic : sres[cfound] + "," + mic;
      } while (true);

      return new Object[]{cmap, sres};
    }
  }

  private static int find(final int[] cmap, final int cidx, final int countryIso) {
    int i = 0;
    for (; i <= cidx && cmap[i] != countryIso; i++) {
      ;
    }
    if (cmap[i] == 0) {
      return -1;
    } else {
      return i;
    }
  }
}
