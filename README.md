game ini adalah game pertama kali saya buat
percobaan aja memakai bahasa java idea
Komponen	Kegunaan dalam Proyek Kita	Detail
Java Development Kit (JDK)	Bahasa pemrograman inti yang digunakan untuk menulis kode.	Kita menggunakan JDK 8 (Java 8) karena merupakan versi yang paling stabil dan direkomendasikan untuk proyek LibGDX Anda (versi 1.14.0).
Gradle	Alat otomatisasi build untuk mengelola dependensi dan menjalankan game.	Kita menggunakan perintah ./gradlew lwjgl3:run untuk mengkompilasi dan menjalankan game desktop. Kita juga menggunakan ./gradlew --stop untuk memaksa Gradle menggunakan JDK 8 dan menghindari error Unsupported class file major version dari Java 17.
Hiero v5	Alat untuk mengubah file font TrueType (.ttf) menjadi format yang dapat digunakan LibGDX.	Digunakan untuk mengonversi Roboto-Regular.ttf menjadi pasangan file roboto_game.fnt (data glyph) dan roboto_game.png (tekstur font).
IDE/Terminal	Lingkungan kerja utama (misalnya, Visual Studio Code).	Digunakan untuk menulis kode Java, mengedit konfigurasi, dan menjalankan perintah Gradle di terminal.
Komponen,Kelas (Contoh),Kegunaan
Game Utama,ColorReactionGame.java,"Kelas utama yang mewarisi ApplicationAdapter, berisi semua logika game, render, dan update."
Rendering,SpriteBatch,Digunakan untuk menggambar tekstur font (roboto_game.png) ke layar.
Font,BitmapFont,Digunakan untuk memuat dan menampilkan teks dari file .fnt dan .png.
Pengaturan Jendela,Lwjgl3Launcher.java,"Digunakan untuk mengatur parameter game desktop, termasuk ukuran jendela awal (kita perbesar menjadi 1024x768) untuk mencegah teks terpotong."
Pusat Teks,GlyphLayout,Digunakan untuk mengukur dimensi teks yang akan dicetak oleh BitmapFont. Ini adalah kunci untuk menempatkan teks tepat di tengah layar (centering) secara horizontal.
Waktu Akurat,TimeUtils.nanoTime(),"Digunakan untuk mengukur waktu reaksi pemain dengan presisi nanodetik, menghasilkan skor dalam satuan milidetik (ms)."
Fitur,Deskripsi,Komponen Kode Utama
Mode Dasar,"Logika dasar permainan, menunggu waktu acak (timeToChange) sebelum layar berubah dan meminta klik.","timeToChange, isWaitingForClick, challengeStartTime."
Mode Terlalu Cepat,Pemain mengklik sebelum waktu tunggu selesai.,"Logika di render() yang memeriksa if (message.equals(""Tunggu warna berubah..."")) saat ada input klik."
Mode Terlalu Lambat (Baru),"Menambahkan batas waktu 2000 ms (2 detik) setelah sinyal !!! KLIK SEKARANG !!! muncul. Jika terlewat, pemain kalah.",Konstanta MAX_REACTION_TIME dan pemeriksaan elapsedMilliSeconds > MAX_REACTION_TIME di render().
Peningkatan Kesulitan (Dinamis),Kesulitan meningkat seiring skor terbaik (bestReactionTime) membaik.,Fungsi resetGame() dan getChallengingColor().
Waktu Tunggu Lebih Cepat,Waktu tunggu berikutnya (timeToChange) dipersingkat jika skor terbaik pemain sangat rendah (cepat).,Logika penskalaan maxTime berdasarkan bestReactionTime / CHALLENGE_SCORE_THRESHOLD di resetGame().
Warna Lebih Mirip,"Warna target baru dibuat dengan jarak Hue yang sangat dekat dengan warna sebelumnya, membuatnya sulit dibedakan.",Logika penskalaan hueRange berdasarkan bestReactionTime di getChallengingColor().
TERIMAKASIH
