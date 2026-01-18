import model.*;
import manager.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Main Class - Entry Point Aplikasi
 * Testing semua class yang sudah dibuat
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  APLIKASI MANAJEMEN JADWAL KEGIATAN BELAJAR");
        System.out.println("==============================================\n");

        // Inisialisasi Manager
        JadwalManager jadwalManager = new JadwalManager();
        NotifikasiManager notifikasiManager = new NotifikasiManager(jadwalManager);
        StatistikManager statistikManager = new StatistikManager(jadwalManager);

        // ========== TEST 1: TAMBAH KEGIATAN BELAJAR ==========
        System.out.println(">>> TEST 1: TAMBAH KEGIATAN BELAJAR\n");

        KegiatanBelajar belajar1 = new KegiatanBelajar(
                0, // ID akan di-set otomatis oleh manager
                "Belajar Polimorfisme",
                LocalDate.of(2026, 1, 15),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                "Pemrograman Berorientasi Objek",
                "Polimorfisme & Abstract Class"
        );
        belajar1.ubahStatus("Selesai");
        jadwalManager.tambahKegiatan(belajar1);

        KegiatanBelajar belajar2 = new KegiatanBelajar(
                0,
                "Belajar Basis Data",
                LocalDate.of(2026, 1, 15),
                LocalTime.of(13, 0),
                LocalTime.of(15, 0),
                "Basis Data",
                "Normalisasi Database"
        );
        jadwalManager.tambahKegiatan(belajar2);

        System.out.println(belajar1.tampilkanDetail());
        System.out.println("\n" + belajar2.tampilkanDetail());
        System.out.println("\n✅ Berhasil menambahkan 2 kegiatan belajar\n");

        // ========== TEST 2: TAMBAH TUGAS ==========
        System.out.println(">>> TEST 2: TAMBAH TUGAS\n");

        KegiatanTugas tugas1 = new KegiatanTugas(
                0,
                "Tugas Project PBO",
                LocalDate.of(2026, 1, 15),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0),
                "Pemrograman Berorientasi Objek",
                LocalDate.of(2026, 1, 20),
                "Tinggi"
        );
        tugas1.updateProgress(65);
        jadwalManager.tambahKegiatan(tugas1);

        KegiatanTugas tugas2 = new KegiatanTugas(
                0,
                "Tugas ERD Database",
                LocalDate.of(2026, 1, 16),
                LocalTime.of(14, 0),
                LocalTime.of(16, 0),
                "Basis Data",
                LocalDate.of(2026, 1, 18),
                "Sedang"
        );
        tugas2.updateProgress(30);
        jadwalManager.tambahKegiatan(tugas2);

        System.out.println(tugas1.tampilkanDetail());
        System.out.println("\n" + tugas2.tampilkanDetail());
        System.out.println("\n✅ Berhasil menambahkan 2 tugas\n");

        // ========== TEST 3: TAMBAH UJIAN ==========
        System.out.println(">>> TEST 3: TAMBAH UJIAN\n");

        KegiatanUjian ujian1 = new KegiatanUjian(
                0,
                "UTS Struktur Data",
                LocalDate.of(2026, 1, 20),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                "Struktur Data",
                "Lab 301",
                "UTS",
                "Tree, Graph, Sorting"
        );
        jadwalManager.tambahKegiatan(ujian1);

        System.out.println(ujian1.tampilkanDetail());
        System.out.println("\n✅ Berhasil menambahkan ujian\n");

        // ========== TEST 4: POLIMORFISME ==========
        System.out.println(">>> TEST 4: POLIMORFISME (Method Overriding)\n");
        System.out.println("Memanggil method tampilkanDetail() dari berbagai objek:\n");

        for (Kegiatan k : jadwalManager.getDaftarKegiatan()) {
            System.out.println(k.tampilkanDetail());
            System.out.println("\n" + "=".repeat(50) + "\n");
        }

        // ========== TEST 5: JADWAL MANAGER ==========
        System.out.println(">>> TEST 5: JADWAL MANAGER - FILTER & SEARCH\n");

        System.out.println("📅 Kegiatan Hari Ini:");
        for (Kegiatan k : jadwalManager.getKegiatanHariIni()) {
            System.out.println("  - " + k.getJudul() + " (" + k.getWaktuMulai() + ")");
        }

        System.out.println("\n📚 Kegiatan Mata Kuliah PBO:");
        for (Kegiatan k : jadwalManager.getKegiatanByMataKuliah("Pemrograman Berorientasi Objek")) {
            System.out.println("  - " + k.getJudul());
        }

        System.out.println("\n🔍 Search 'Belajar':");
        for (Kegiatan k : jadwalManager.searchKegiatan("Belajar")) {
            System.out.println("  - " + k.getJudul());
        }

        System.out.println("\n✅ Total kegiatan: " + jadwalManager.getTotalKegiatan() + "\n");

        // ========== TEST 6: NOTIFIKASI MANAGER ==========
        System.out.println(">>> TEST 6: NOTIFIKASI & REMINDER\n");

        notifikasiManager.cekDeadline();
        System.out.println("🔔 Notifikasi Aktif: " + notifikasiManager.getJumlahNotifikasi());
        System.out.println("📊 Summary: " + notifikasiManager.getSummaryNotifikasi());
        System.out.println();

        for (String notif : notifikasiManager.getNotifikasiAktif()) {
            System.out.println("  " + notif);
        }
        System.out.println();

        // ========== TEST 7: STATISTIK MANAGER ==========
        System.out.println(">>> TEST 7: STATISTIK & ANALISIS\n");

        System.out.println("📊 STATISTIK BELAJAR:");
        System.out.println("  - Total Jam Belajar Minggu Ini: " +
                String.format("%.1f", statistikManager.hitungJamBelajarMingguIni()) + " jam");

        System.out.println("\n📚 JAM BELAJAR PER MATA KULIAH:");
        statistikManager.getJamBelajarPerMataKuliah().forEach((mk, jam) ->
                System.out.println("  - " + mk + ": " + String.format("%.1f", jam) + " jam")
        );

        System.out.println("\n📝 STATUS TUGAS:");
        statistikManager.getStatusTugas().forEach((status, jumlah) ->
                System.out.println("  - " + status + ": " + jumlah)
        );

        System.out.println("\n🎯 PRODUKTIVITAS:");
        System.out.println("  - Skor: " + statistikManager.analisisProduktivitas() + "%");
        System.out.println("  - Status: " + statistikManager.getLabelProduktivitas());

        System.out.println("\n" + statistikManager.getSummaryStatistik());

        // ========== TEST 8: ENKAPSULASI ==========
        System.out.println(">>> TEST 8: ENKAPSULASI (Getter/Setter)\n");

        MataKuliah mk = new MataKuliah(
                "IF101",
                "Pemrograman Berorientasi Objek",
                3,
                "Dr. John Doe",
                "Lab 201"
        );

        System.out.println("Mata Kuliah: " + mk);
        System.out.println("  - Kode: " + mk.getKodeMK());
        System.out.println("  - Nama: " + mk.getNamaMK());
        System.out.println("  - SKS: " + mk.getSks());
        System.out.println("  - Dosen: " + mk.getDosen());
        System.out.println("  - Ruangan: " + mk.getRuangan());

        User user = new User("123456789", "Budi Santoso", 5, "Teknik Informatika");
        System.out.println("\nUser: " + user);
        System.out.println("  - NIM: " + user.getNim());
        System.out.println("  - Nama: " + user.getNama());
        System.out.println("  - Semester: " + user.getSemester());
        System.out.println("  - Prodi: " + user.getProgramStudi());

        System.out.println("\n✅ Enkapsulasi berhasil - Data diakses via getter/setter\n");

        // ========== KESIMPULAN ==========
        System.out.println("==============================================");
        System.out.println("         ✅ SEMUA TEST BERHASIL!");
        System.out.println("==============================================");
        System.out.println("\n3 KONSEP OOP BERHASIL DITERAPKAN:");
        System.out.println("  ✓ ENKAPSULASI - Atribut private + getter/setter");
        System.out.println("  ✓ PEWARISAN - Kegiatan → KegiatanBelajar/Tugas/Ujian");
        System.out.println("  ✓ POLIMORFISME - Override method tampilkanDetail()");
        System.out.println("\n==============================================\n");
    }
}