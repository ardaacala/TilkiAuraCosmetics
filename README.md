# AuraCosmetics

Spigot 1.21.x ve Java 21 için, tag ile açılan üç özgün dokulu kozmetik aura. Aura görselleri Minecraft parçacık dokularını kullanmaz; birlikte verilen kaynak paketindeki dokuz bitmap glif `TextDisplay` ile çizilir. Shader olmadan parlak görünmesi için dokuların içine ışık haleleri işlendi ve display parlaklığı tam seviyeye ayarlandı. Gerçek shader bloom etkisi, oyuncunun shader ayarlarına bağlıdır.

## Auralar

| Aura | Açan TilkiTags rütbeleri | Görsel |
| --- | --- | --- |
| `vip` | VIP, VIP+, MegaVIP, MegaVIP+, Tilki, Kurucu | Dönen iki altın halka ve yükselen parıltılar |
| `megavip` | MegaVIP, MegaVIP+, Tilki, Kurucu | Mor zemin mührü, dönen kristaller ve enerji sarmalı |
| `tilki` | Tilki, Kurucu | Ateş tilkisi, taç, zemin halkası ve alevler |

Aura, oyuncunun TilkiTags rütbesiyle (LuckPerms grubu, ör. `/lp user Arda parent add tilki`) açılır. Liste `access.ranks` bölümünden değiştirilebilir. Ayrıca `auracosmetics.aura.<aura>` izni veya eski yöntem olarak Minecraft scoreboard tag'i de aurayı açar (`access.tags`):

```mcfunction
/tag Oyuncu add VIP
/tag Oyuncu add MegaVIP
/tag Oyuncu add Tilki
```

Tag eşleşmesi büyük/küçük harfe duyarlıdır. Oyuncunun `/aura` komutu için `auracosmetics.use` iznine de sahip olması gerekir; bu izin varsayılan olarak herkese açıktır. Operatörlük tag şartını kaldırmaz. Tag silinince aura en geç sonraki animasyon adımında görünmez olur.

## İndirme

Hazır JAR GitHub sürümlerindedir; derlemeye gerek yoktur:

- **[AuraCosmetics-1.1.0.jar](https://github.com/ardaacala/TilkiAuraCosmetics/releases/download/v1.1.0/AuraCosmetics-1.1.0.jar)**
- Tüm sürümler: [Releases](https://github.com/ardaacala/TilkiAuraCosmetics/releases/latest)

## Kurulum

1. [Sürümler sayfasından](https://github.com/ardaacala/TilkiAuraCosmetics/releases/latest) `AuraCosmetics-1.1.0.jar` dosyasını indirip sunucunun `plugins/` klasörüne koyun. Kendiniz derlemek isterseniz aşağıdaki yerel derleme adımları `target/AuraCosmetics-1.1.0.jar` üretir.
2. Aura dokuları TilkiResourcePack 1.0.22 ve sonrasının birleşik paketinde gelir; ayrı paket gerekmez. Glifler `ED00–ED08` aralığındadır (giriş ekranının `E900` aralığıyla çakışmaz). Eski `dist/AuraCosmetics-TilkiInterface-ResourcePack.zip`, TilkiInterface'in eski dokularını içerdiği için artık dağıtılmamalıdır.
3. Oyuncuya ilgili rütbeyi verin, ardından `/aura` menüsünden veya `/aura set vip|megavip|tilki` ile aurayı açın.

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
