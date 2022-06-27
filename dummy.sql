INSERT INTO role (id, name) VALUES (1, 0);
INSERT INTO role (id, name) VALUES (2, 1);
INSERT INTO m_city (id, created_at, is_deleted, updated_at, name) VALUES (1, now(), false, now(), 'Jakarta Pusat'),
                                                                         (2, now(), false, now(), 'Jakarta Barat'),
                                                                         (3, now(), false, now(), 'Jakarta Timur'),
                                                                         (4, now(), false, now(), 'Jakarta Utara');
INSERT INTO m_complex (id, created_at, is_deleted, updated_at, name, city_id) VALUES (1, now(), false, now(), 'SCBD', 1),
                                                                                     (2, now(), false, now(), 'Tanah Abang', 1),
                                                                                     (3, now(), false, now(), 'Senayan City', 1),
                                                                                     (4, now(), false, now(), 'Kuningan', 2),
                                                                                     (5, now(), false, now(), 'Sudirman', 2),
                                                                                     (6, now(), false, now(), 'Pluit', 3),
                                                                                     (7, now(), false, now(), 'Tanjung Priok', 3),
                                                                                     (8, now(), false, now(), 'Kebon Jeruk', 4),
                                                                                     (9, now(), false, now(), 'Cengkareng', 4);
INSERT INTO m_building (id, created_at, is_deleted, updated_at, address, building_size, capacity, description, name, complex_id)
VALUES (2, now(), false, now(), 'Jalan Tanah Abang II No. 49-51', '490 x 440 m2', 800, 'Kantor ini merupakan alternatif yang baik bagi Anda yang mencari ruangan berukuran sedang yang dapat menampung hingga 65 orang. Terletak di pusat kota Jakarta, gedung ini terletak di dekat kawasan perkantoran dan dikelilingi oleh beberapa pilihan tempat makan dan hiburan. Ketika Anda memiliki waktu luang yang terbatas, istirahat sejenak di kafetaria di dalam gedung.

Jangan ragu untuk membawa kendaraan sendiri karena gedung berlantai empat ini dilengkapi dengan area parkir basement. Area ini akan memungkinkan Anda untuk menghindari area kebijakan ganjil genap dengan mengambil akses dari Abdul Muis, Balaikota, Kemayoran. Atau, Anda juga dapat memilih transportasi umum seperti bus dan berjalan kaki selama beberapa menit untuk mencapai kantor.

Ruang kantor ini ditempatkan khusus di lantai satu. Gedung dilengkapi dengan lift. Harga yang tertera di website ini sudah termasuk harga dasar sebesar 165.000/m2 ditambah dengan service charge sebesar', 'Graha Dinamika', 3),
    (4, now(), false, now(), 'Jl. Prof. DR. Satrio No.7, Kel. Karet Kuningan, Kec. Setiabudi, Jakarta Selatan', '430 x 440 m2', 330, 'Ruang untuk Memulai, Ruang untuk Berkembang Selamat datang di JSCHive by CoHive, dijalankan oleh ekosistem ruang bersama terintegrasi yang paling cepat berkembang di Indonesia Ruang kerja bersama CoHive dibangun dengan harapan dapat membangun komunitas di mana setiap orang dapat menikmati pekerjaan sebanyak mungkin Anggota CoHive nikmati lingkungan yang mendukung seperti. Bekerja di kantor pribadi mereka, meja kerja rekan kerja, ruang rapat, atau ruang acara dan pastikan waktu yang produktif. Orang-orang yang berpikiran, lengkap dengan internet berkecepatan tinggi dan kopi gratis. JSCHive by CoHive adalah coworking space, event space, dan private office center digabung menjadi satu. Dengan berjalan kaki singkat adalah Tokopedia Tower, Ciputra World 2, dan berbagai pilihan belanja dan makan di Lotte Shopping Avenue. Kuningan adalah tempat orang dapat menemukan beberapa kedutaan seperti Australia, Malaysia, Singapura dan Swiss antara lain membuat pemandangan yang lebih beragam. dengan bar dan restoran trendi untuk menghibur sebagian besar orang yang mengunjungi ibu kota.', 'JSCHive', 1),
    (1, now(), false, now(), 'Jalan Cut Mutia No. 9, Senayan', '340 x 340 m2', 400, 'Sofyan Hotel adalah pelopor hotel syariah di Indonesia. Dibangun pada tahun 1968, hotel ini telah menyediakan layanan yang luar biasa bagi para tamunya selama lebih dari 50 tahun. Jika Anda sedang mencari lokasi yang sempurna untuk kantor virtual Anda, Sofyan Hotel akan menjadi salah satu pilihan karena akan mengakomodasi alamat bisnis Anda, layanan resepsionis, serta ruang pertemuan gratis selama 20 jam per tahun. Dengan pengalaman tersebut, Sofyan Hotel akan membantu Anda memberikan pelayanan yang efisien untuk kebutuhan bisnis Anda. Di kawasan pusat ibu kota terletak sebagian besar bangunan penting pemerintah dan landmark, termasuk kawasan perumahan presiden, Istana Negara, dan Monumen Nasional. Harapkan perjalanan Anda di sini untuk merasakan kembali masa lalu Jakarta.', 'Sofyan Hotel', 3),
    (3, now(), false, now(), 'Menara Palma, Kuningan', '500 x 500 m2', 330, 'Dengan 3000 pusat bisnis di 900 kota yang berbeda, Regus adalah salah satu penyedia solusi ruang kerja fleksibel terbesar di dunia. Menyediakan lingkungan untuk pengaturan profesional apa pun, Regus akan menjadi pilihan sempurna untuk kebutuhan bisnis Anda. Baik Anda membutuhkan kantor pribadi, Di Regus --Menara Palma, manfaatkan ruang yang memiliki semua yang Anda butuhkan: internet berkecepatan tinggi, lingkungan kerja yang lapang dan profesional, saluran telepon, dan dukungan dari tim kami Regus --Menara Palma terletak di Kuningan, tempat internasional yang mewah hotel dan apartemen, kedutaan besar asing, tempat makan dan pusat perbelanjaan juga dipartisi.Dekat dengan cabang berbagai bank (Bank Mandiri, Bank Internasional Indonesia, Bank Central Asia, dan banyak lagi), serta pilihan belanja dan makan di Kuningan City, Duta ITC, Epicentrum Walk, dan Plaza Festival Regus --Menara Palma adalah solusi sempurna untuk bisnis ness yang ingin menikmati pemandangan cakrawala Jakarta yang luar biasa dan kantor yang lengkap dengan layanan yang sangat baik.', 'Menara Palma', 2);

INSERT INTO m_floor (id, created_at, is_deleted, updated_at, image, max_capacity, name, size, starting_price, type, building_id)
VALUES (1, now(), false, now(), 'e6b60773-ba1e-435b-a882-6b844e1e2209.jpg', 12, 'Floor 1', '300 x 300 m2', 4000000, 'Serviced Office', 1),
    (2, now(), false, now(), 'fc8b71d5-ea18-4f3e-823d-4f94415f40cb.jpg', 30, 'Floor 2', '300 x 300 m2', 9000000, 'Office Space', 1),
    (3, now(), false, now(), 'e6b60773-ba1e-435b-a882-6b844e1e2209.jpg', 25, 'Floor 1', '400 x 700 m2', 6000000, 'Event Space', 2),
    (4, now(), false, now(), 'fc8b71d5-ea18-4f3e-823d-4f94415f40cb.jpg', 7, 'Floor 1', '400 x 700 m2', 6500000, 'Office Space', 2),
    (5, now(), false, now(), 'e6b60773-ba1e-435b-a882-6b844e1e2209.jpg', 20, 'Floor 1', '400 x 700 m2', 5400000, 'Co-Working Office', 3),
    (6, now(), false, now(), 'fc8b71d5-ea18-4f3e-823d-4f94415f40cb.jpg', 10, 'Floor 1', '400 x 700 m2', 2300000, 'Co-Working Office', 3),
    (7, now(), false, now(), 'e6b60773-ba1e-435b-a882-6b844e1e2209.jpg', 1, 'Floor 1', '400 x 700 m2', 1700000, 'Event Space', 4),
    (8, now(), false, now(), 'fc8b71d5-ea18-4f3e-823d-4f94415f40cb.jpg', 1, 'Floor 1', '400 x 700 m2', 3300000, 'Serviced Office', 4);

INSERT INTO facilities (building_id, facilities) VALUES (1, 'WIFI'),
                                                     (1, 'BANK'),
                                                     (1, 'PROJECTOR'),
                                                     (2, 'WIFI'),
                                                     (2, 'PARKING'),
                                                     (3, 'PROJECTOR'),
                                                     (3, 'WIFI'),
                                                     (3, 'RESTAURANT'),
                                                     (3, 'BANK'),
                                                     (4, 'WIFI'),
                                                     (4, 'PROJECTOR');
INSERT INTO m_building_images (id, file_name, building_id) VALUES (1, '2d38f3e2-8cbf-45cc-a7f7-e5d302958afc-0.jpg', 1),
                                                                 (2, '538b593f-7b3f-440c-9bfe-40ed073e9e29.jpg', 1),
                                                                 (3, '2d38f3e2-8cbf-45cc-a7f7-e5d302958afc-0.jpg', 2),
                                                                 (4, '538b593f-7b3f-440c-9bfe-40ed073e9e29.jpg', 3),
                                                                 (5, '2d38f3e2-8cbf-45cc-a7f7-e5d302958afc-0.jpg', 4),
                                                                 (6, '538b593f-7b3f-440c-9bfe-40ed073e9e29.jpg', 4),
                                                                 (7, '2d38f3e2-8cbf-45cc-a7f7-e5d302958afc-0.jpg', 4);



