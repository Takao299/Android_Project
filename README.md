# Android_Project

【IDE】
Android Studio Koala Feature Drop | 2024.1.2
【エミュレータデバイス】
Pixel_6_API_33


------ サーバー接続／非接続について ------
Androidアプリをサーバー非接続で使用する場合は、
com.example.spbtex.sqlite.DbOpenHelpernの
フィールドSTAND_ALONEをtrueに書き換えてください。


------ AWS利用について ------
AWSを利用する場合、
Androidアプリの方は設定を書き込む必要があります。

network_security_config.xmlと、
com.example.spbtex.UrlsのフィールドSERVERに、
IPアドレスまたはドメインを書き込みます。


AWSの構築につきましては、
本「SpringBootで学ぶAWS開発入門」を参考にさせて頂いております。