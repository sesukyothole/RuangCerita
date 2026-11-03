package com.example.data

object PreloadedStories {
    val GENRES = listOf(
        "Semua",
        "Romantis",
        "Horor",
        "Komedi",
        "Fantasi",
        "Misteri",
        "Petualangan",
        "Kehidupan",
        "Sci-Fi"
    )

    fun getInitialStories(): List<StoryEntity> {
        return listOf(
            StoryEntity(
                id = "blogger_init_1",
                title = "Di Sudut Kafe Senja",
                author = "Ruang Cerita",
                genre = "Romantis",
                summary = "Pertemuan tak terduga di tengah rintik hujan sore hari membuka kembali lembaran kenangan masa lalu.",
                content = """
                    Hujan sore itu turun dengan deras di atas atap seng kafe kecil di persimpangan jalan. Aroma kopi tubruk dan tanah basah berpadu, menciptakan suasana tenang yang sudah lama kurindukan. Di meja sudut dekat jendela kaca yang berembun, aku duduk menatap tetesan air yang berlomba menuruni kaca.

                    Pintu kafe berdenting pelan. Seseorang masuk dengan mantel abu-abu yang sedikit basah. Ketika pandangan kami saling berpapasan, waktu seolah berhenti berdetak. Itu dia—sosok yang lima tahun lalu pamit untuk mengejar mimpinya di negeri seberang tanpa pernah memberi kabar pasti.

                    "Boleh aku duduk di sini?" tanyanya lembut dengan senyum yang sama persis seperti dulu.

                    Aku tersenyum tipis dan mengangguk. Ada jutaan kata yang ingin kuucapkan, namun di saat itu, sebuah anggukan kecil sudah mewakili ribuan rasa rindu yang tersimpan rapi.
                """.trimIndent(),
                publishedDate = "2024-01-15",
                readTimeMinutes = 4,
                isFavorite = false,
                isSyncedFromWeb = true,
                sourceUrl = "https://iseng-isex.blogspot.com",
                isRead = false
            ),
            StoryEntity(
                id = "blogger_init_2",
                title = "Langkah Kaki di Lorong Sunyi",
                author = "Ruang Cerita",
                genre = "Horor",
                summary = "Malam sunyi di gedung arsip tua menyimpan rahasia yang tak pernah terungkap selama puluhan tahun.",
                content = """
                    Lampu neon di lantai tiga gedung arsip tua itu berkedip-kedip tidak menentu. Jam dinding sudah menunjukkan pukul sebelas malam. Sebagai staf penjaga malam baru, tugasku hanya memastikan semua jendela dan pintu terkunci rapat.

                    Namun, setiap kali aku melewati lorong sempit menuju ruang arsip nomor 13, selalu terdengar derap langkah kaki pelan di belakangku. Suaranya berirama: *tap... tap... tap...*

                    Aku berhenti mendadak. Suara itu pun berhenti. Saat aku berbalik pelan, tidak ada seorang pun di sana—hanya bayang-bayang lemari besi yang berdiri kaku dalam keremangan. Ketika aku kembali melangkah, sebuah hembusan nafas dingin menyentuh tengkuk leherku, diikuti bisikan lirih: "Tolong carikan berkasku..."
                """.trimIndent(),
                publishedDate = "2024-02-10",
                readTimeMinutes = 5,
                isFavorite = false,
                isSyncedFromWeb = true,
                sourceUrl = "https://iseng-isex.blogspot.com",
                isRead = false
            ),
            StoryEntity(
                id = "blogger_init_3",
                title = "Kekacauan di Dapur Chef Budi",
                author = "Ruang Cerita",
                genre = "Komedi",
                summary = "Insiden garam yang tertukar dengan gula memicu kehebohan di restoran bintang lima.",
                content = """
                    Chef Budi terkenal sebagai koki paling disiplin dan perfeksionis se-kota. Tak boleh ada kesalahan sebutir beras pun di dapurnya. Namun hari ini adalah hari perayaan ulang tahun restoran yang ke-10, dan tamu kehormatan dari kantor wali kota sudah menanti hidangan sup buntut legendarisnya.

                    Di tengah kepanikan dan hiruk pikuk pesanan, asisten barunya, Joni, menuangkan satu mangkuk bumbu putih ke dalam kuali besar. "Bagus, garamnya sudah cukup!" seru Chef Budi sambil mengaduk cepat tanpa mencicipi terlebih dahulu.

                    Lima menit kemudian, sang wali kota menyeruput sendok pertamanya. Wajahnya berubah dari santai menjadi terbelalak kaget. "Luar biasa unik, Chef! Baru kali ini saya makan sup buntut dengan cita rasa kolak pisang!" Chef Budi pingsan di tempat.
                """.trimIndent(),
                publishedDate = "2024-03-05",
                readTimeMinutes = 3,
                isFavorite = false,
                isSyncedFromWeb = true,
                sourceUrl = "https://iseng-isex.blogspot.com",
                isRead = false
            ),
            StoryEntity(
                id = "blogger_init_4",
                title = "Pintu Menuju Dimensi Aethelgard",
                author = "Ruang Cerita",
                genre = "Fantasi",
                summary = "Sebuah jam saku kuno warisan kakek menjadi kunci pembuka gerbang kerajaan langit.",
                content = """
                    Elian tak pernah menyangka bahwa jam saku perak pemberian kakeknya bukan sekadar penunjuk waktu. Jarum detiknya berputar berlawanan arah tepat saat gerhana matahari cincin terjadi di langit barat.

                    Sebuah retakan cahaya keemasan muncul di udara kamar tidurnya, membentang selebar pintu. Dari dalam retakan itu tercium wangi angin pegunungan salju dan gemerlap pulau-pulau melayang di langit biru cerah.

                    "Selamat datang kembali, Pewaris Penjaga Waktu," terdengar suara berat seekor griffin berbulu zamrud yang bertengger di atas batu gerbang. Petualangan baru yang melampaui batas logika baru saja dimulai.
                """.trimIndent(),
                publishedDate = "2024-04-12",
                readTimeMinutes = 6,
                isFavorite = false,
                isSyncedFromWeb = true,
                sourceUrl = "https://iseng-isex.blogspot.com",
                isRead = false
            ),
            StoryEntity(
                id = "blogger_init_5",
                title = "Koper Hitam Tanpa Pemilik",
                author = "Ruang Cerita",
                genre = "Misteri",
                summary = "Sebuah koper tertinggal di gerbong kereta api komuter membawa teka-teki konspirasi kota.",
                content = """
                    Gerbong kereta terakhir Stasiun Manggarai sudah kosong melompong ketika petugas kebersihan menemukan sebuah koper kulit hitam berangkaian kunci kombinasi rumit di rak bagasi atas.

                    Di bagian pegangan koper terdapat secarik kertas bertuliskan huruf stempel: *Buka sebelum fajar, atau rahasia ini lenyap selamanya.*

                    Detektif Arya yang dipanggil ke lokasi segera memeriksa nomor seri koper tersebut. Anehnya, kode tersebut cocok dengan insiden pencurian brankas bank sentral sepuluh tahun silam yang pelakunya tidak pernah tertangkap hingga detik ini.
                """.trimIndent(),
                publishedDate = "2024-05-20",
                readTimeMinutes = 5,
                isFavorite = false,
                isSyncedFromWeb = true,
                sourceUrl = "https://iseng-isex.blogspot.com",
                isRead = false
            ),
            StoryEntity(
                id = "blogger_init_6",
                title = "Secangkir Teh di Pagi Hari",
                author = "Ruang Cerita",
                genre = "Kehidupan",
                summary = "Pelajaran sederhana tentang bersyukur dan menemukan ketenangan di tengah hiruk pikuk kehidupan kota.",
                content = """
                    Setiap pagi sebelum matahari menyapa cakrawala, Pak Darmo selalu menyeduh secangkir teh melati hangat di teras rumahnya yang rimbun oleh tanaman suplir.

                    Bagi pensiunan guru itu, kebahagiaan sejati tidak diukur dari seberapa cepat kita berlari mengejar dunia, melainkan seberapa dalam kita mampu menikmati setiap nafas dan berkah yang diberikan hari ini.

                    "Dunia selalu bising, Nak," ujarnya pada cucunya yang sedang bersiap ke kampus. "Tetapi hatimu adalah rumahmu sendiri. Jangan biarkan kebisingan luar merusak kedamaian di dalam."
                """.trimIndent(),
                publishedDate = "2024-06-01",
                readTimeMinutes = 4,
                isFavorite = false,
                isSyncedFromWeb = true,
                sourceUrl = "https://iseng-isex.blogspot.com",
                isRead = false
            )
        )
    }
}
