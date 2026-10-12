# AuraCosmetics

Spigot 1.21.x ve Java 21 için, tag ile açılan üç özgün dokulu kozmetik aura. Aura görselleri Minecraft parçacık dokularını kullanmaz; birlikte verilen kaynak paketindeki dokuz bitmap glif `TextDisplay` ile çizilir. Shader olmadan parlak görünmesi için dokuların içine ışık haleleri işlendi ve display parlaklığı tam seviyeye ayarlandı. Gerçek shader bloom etkisi, oyuncunun shader ayarlarına bağlıdır.

## Auralar

| Aura | Tag | Görsel |
| --- | --- | --- |
| `vip` | `VIP` | Dönen iki altın halka ve yükselen parıltılar |
| `megavip` | `MegaVIP` | Mor zemin mührü, dönen kristaller ve enerji sarmalı |
| `tilki` | `Tilki` | Ateş tilkisi, taç, zemin halkası ve alevler |

Tag adları `plugins/AuraCosmetics/config.yml` içindeki `access.tags` bölümünden değiştirilebilir. Varsayılanlar Minecraft scoreboard tag'leridir:

```mcfunction
/tag Oyuncu add VIP
/tag Oyuncu add MegaVIP
/tag Oyuncu add Tilki
```

Tag eşleşmesi büyük/küçük harfe duyarlıdır. Oyuncunun `/aura` komutu için `auracosmetics.use` iznine de sahip olması gerekir; bu izin varsayılan olarak herkese açıktır. Operatörlük tag şartını kaldırmaz. Tag silinince aura en geç sonraki animasyon adımında görünmez olur.

## Kurulum

1. Java 21 ile projeyi derleyin ve `target/AuraCosmetics-1.0.0.jar` dosyasını sunucunun `plugins/` klasörüne koyun.
2. `dist/AuraCosmetics-TilkiInterface-ResourcePack.zip` arşivini istemcilere kaynak paketi olarak verin. Bu arşiv mevcut TilkiInterface dokularını da içerir. Yalnızca JAR kurulursa özgün glifler görünmez.
3. Kaynak paketinin `pack.mcmeta` dosyası mevcut projedeki gibi Minecraft **1.21.4** için format 46 kullanır. Farklı istemci sürümleri için paket formatını uyarlayın ve oyunda görünümü doğrulayın.
4. Oyuncuya ilgili tag'i verin, ardından `/aura` menüsünden veya `/aura set vip|megavip|tilki` ile aurayı açın.

Yerel derleme:

```powershell
$env:JAVA_HOME = (Resolve-Path '.tools/jdk-21.0.12.1+1').Path
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
& '.tools/apache-maven-3.9.9/bin/mvn.cmd' '-Dmaven.repo.local=.m2' package
```

Dokuları yeniden üretmek için Java 21 ile `java tools/GenerateAuraTextures.java` çalıştırın. PNG çıktıları `resourcepack/assets/auracosmetics/textures/font/` içine yazılır. Ardından `powershell -NoProfile -ExecutionPolicy Bypass -File tools/BuildResourcePack.ps1` ile ZIP ve SHA-1 dosyasını yeniden oluşturun.

## Komutlar

`/aura` menüyü açar. `/aura list`, `/aura set <aura>`, `/aura off`, `/aura toggle` oyuncular içindir. `/aura reload` yönetici izni (`auracosmetics.admin`) ister ve tag ayarları ile animasyon aralığını yeniden yükler.

Seçim UUID ile saklanır. Çıkış, ölüm, dünya değişimi, görünmezlik ayarı, tag kaybı ve eklenti kapanışı sırasında display nesneleri temizlenir. Eski aura seçimleri ilk yüklemede bilinmeyen ID olarak atılır. Her etkin oyuncu için VIP 8, MegaVIP 21, Tilki 16 display kullanır. Yoğun sunucularda görüş mesafesi ve oyuncu sayısına göre performansı oyunda ölçün.
