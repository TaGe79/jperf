# Simple csv parsing performance tests

```
[0.012s][info][gc] Resizeable heap; starting at 128M, max: 2048M, step: 128M
[0.012s][info][gc] Using TLAB allocation; max: 4096K
[0.012s][info][gc] Elastic TLABs enabled; elasticity: 1.10x
[0.012s][info][gc] Elastic TLABs decay enabled; decay time: 1000ms
[0.012s][info][gc] Using Epsilon
```

## parseMicFileVanillaJava 

```
Used Memory before: 1.088.776
Memory increased: 529.064
```

## parseMicFileApacheLow 

```
Used Memory before: 1.088.776
Memory increased: 2.437.552
```

## parseMicFileApacheStream 

```
Used Memory before: 1.088.776
Memory increased: 2.795.272
```

# Performance measures - G1 (JRE-8)

```
 0% Scenario{vm=java, trial=0, benchmark=ParseMicFileVanillaJava} 1097043.58 ns; σ=31676.60 ns @ 10 trials
33% Scenario{vm=java, trial=0, benchmark=ParseMicFileApacheLow} 7235339.51 ns; σ=67709.88 ns @ 3 trials
67% Scenario{vm=java, trial=0, benchmark=ParseMicFileApacheStream} 6381279.87 ns; σ=233474.25 ns @ 10 trials

               benchmark   ms linear runtime
  ParseMicFileApachePLow 1.10 ====
   ParseMicFileApacheLow 7.24 ==============================
ParseMicFileApacheStream 6.38 ==========================
```

# Performance measures - G1 (JRE-12)

```
 0% Scenario{vm=java, trial=0, benchmark=ParseMicFileVanillaJava} 1081644.71 ns; σ=3245.00 ns @ 3 trials
33% Scenario{vm=java, trial=0, benchmark=ParseMicFileApacheLow} 7130892.11 ns; σ=125095.61 ns @ 10 trials
67% Scenario{vm=java, trial=0, benchmark=ParseMicFileApacheStream} 6319167.02 ns; σ=358147.43 ns @ 10 trials

               benchmark   ms linear runtime
  ParseMicFileApachePLow 1.08 ====
   ParseMicFileApacheLow 7.13 ==============================
ParseMicFileApacheStream 6.32 ==========================
```

# Performance measures - Epsilon

```
 0% Scenario{vm=java, trial=0, benchmark=ParseMicFileVanillaJava} 1067663,53 ns; σ=24231,48 ns @ 10 trials
33% Scenario{vm=java, trial=0, benchmark=ParseMicFileApacheLow} 7115980,17 ns; σ=280992,58 ns @ 10 trials
67% Scenario{vm=java, trial=0, benchmark=ParseMicFileApacheStream} 6425133,58 ns; σ=60792,50 ns @ 4 trials

               benchmark   ms linear runtime
  ParseMicFileApachePLow 1,07 ====
   ParseMicFileApacheLow 7,12 ==============================
ParseMicFileApacheStream 6,43 ===========================
```

# Performance measures GraalVM (JIT2)

```
tage$ java -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler -jar build/libs/jperf-1.0-SNAPSHOT-all.jar caliper
 0% Scenario{vm=java, trial=0, benchmark=ParseMicFileVanillaJava} 2128807.72 ns; σ=151543.39 ns @ 10 trials
33% Scenario{vm=java, trial=0, benchmark=ParseMicFileApacheLow} 5766523.91 ns; σ=1931877.91 ns @ 10 trials
67% Scenario{vm=java, trial=0, benchmark=ParseMicFileApacheStream} 5751569.34 ns; σ=189130.11 ns @ 10 trials

               benchmark   ms linear runtime
  ParseMicFileApachePLow 2.13 ===========
   ParseMicFileApacheLow 5.77 ==============================
ParseMicFileApacheStream 5.75 =============================
```
