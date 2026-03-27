# GSON Regeln
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }

# Deine Datenmodelle schützen, damit GSON sie (de)serialisieren kann
-keep class com.example.a3dkostenrechner.Material { *; }
-keep class com.example.a3dkostenrechner.Machine { *; }
-keep class com.example.a3dkostenrechner.Spool { *; }
-keep class com.example.a3dkostenrechner.Project { *; }
-keep class com.example.a3dkostenrechner.CalculationSettings { *; }
