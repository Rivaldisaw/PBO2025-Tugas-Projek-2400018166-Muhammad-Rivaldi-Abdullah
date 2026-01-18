import model.*;
import manager.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * MainDemo Class
 * Demo aplikasi dengan data lebih lengkap + Menu interaktif sederhana
 */
public class MainDemo {
    private static JadwalManager jadwalManager;
    private static NotifikasiManager notifikasiManager;
    private static StatistikManager statistikManager;
    private static Scanner scanner;

    public static void main(String[] args) {
        // Inisialisasi
        jadwalManager = new JadwalManager();
        notifikasiManager = new NotifikasiManager(jadwalManager);
        statistikManager = new StatistikManager(jadwalManager);
        scanner = new Scanner(System.in);

        // Load data sample
        loadDataSample();

        // Menu
        boolean running = true;
        while (running) {
            tampilkanMenu();
            int pilihan = inputInteger("Pilih menu: ");

            switch (pilihan) {
                case 1:
                    lihatJadwalHariIni();
                    break;
                case 2:
                    lihatSemuaKegiatan();
                    break;
                case 3:
                    tambahKegiatanBaru();
                    break;
                case 4:
                    lihatNotifikasi();
                    break;
                case 5:
                    lihatStatistik();
                    break;
                case 6:
                    cariKegiatan();
                    break;
                case 0:
                    running = false;
                    System.out.println("\n✅ Terima kasih! Program selesai.\n");
                    break;
                default:
                    System.out.println("\n❌ Pilihan tidak valid!\n");
            }
        }

        scanner.close();
    }

    private static void tampilkanMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("     📚 MANAJEMEN JADWAL KEGIATAN BELAJAR");
        System.out.println("=".repeat(50));
        System.out.println("1. 📅 Lihat Jadwal Hari Ini");
        System.out.println("2. 📋 Lihat Semua Kegiatan");
        System.out.println("3. ➕ Tambah Kegiatan Baru");
        System.out.println("4. 🔔 Lihat Notifikasi");
        System.out.println("5. 📊 Lihat Statistik");
        System.out.println("6. 🔍 Cari Kegiatan");
        System.out.println("0. 🚪 Keluar");
        System.out.println("=".repeat(50));
    }

    private static void lihatJadwalHariIni() {
        System.out.println("\n📅 JADWAL HARI INI (" + LocalDate.now() + ")\n");

        var kegiatanHariIni = jadwalManager.getKegiatanHariIni();

        if (kegiatanHariIni.isEmpty()) {
            System.out.println("✅ Tidak ada kegiatan hari ini.");
        } else {
            for (Kegiatan k : kegiatanHariIni) {
                System.out.println(k.tampilkanDetail());
                System.out.println("\n" + "-".repeat(50) + "\n");
            }
        }

        tekanEnter();
    }

    private static void lihatSemuaKegiatan() {
        System.out.println("\n📋 SEMUA KEGIATAN\n");

        if (jadwalManager.isEmpty()) {
            System.out.println("❌ Belum ada kegiatan.");
        } else {
            System.out.println("Total: " + jadwalManager.getTotalKegiatan() + " kegiatan\n");

            for (Kegiatan k : jadwalManager.getDaftarKegiatan()) {
                String jenis = "";
                if (k instanceof KegiatanBelajar) jenis = "📖 BELAJAR";
                else if (k instanceof KegiatanTugas) jenis = "📝 TUGAS";
                else if (k instanceof KegiatanUjian) jenis = "📚 UJIAN";

                System.out.printf("[%d] %s - %s | %s %s\n",
                        k.getId(), jenis, k.getJudul(),
                        k.getTanggal(), k.getStatus());
            }
        }

        tekanEnter();
    }

    private static void tambahKegiatanBaru() {
        System.out.println("\n➕ TAMBAH KEGIATAN BARU\n");
        System.out.println("Pilih jenis kegiatan:");
        System.out.println("1. Kegiatan Belajar");
        System.out.println("2. Tugas");
        System.out.println("3. Ujian");

        int jenis = inputInteger("Pilihan: ");

        System.out.println("\nMasukkan data kegiatan:");
        String judul = inputString("Judul: ");

        System.out.println("Tanggal (yyyy-mm-dd): ");
        LocalDate tanggal = inputTanggal();

        System.out.println("Waktu mulai (hh:mm): ");
        LocalTime waktuMulai = inputWaktu();

        System.out.println("Waktu selesai (hh:mm): ");
        LocalTime waktuSelesai = inputWaktu();

        String mataKuliah = inputString("Mata Kuliah: ");

        Kegiatan kegiatan = null;

        switch (jenis) {
            case 1:
                String topik = inputString("Topik: ");
                kegiatan = new KegiatanBelajar(0, judul, tanggal, waktuMulai,
                        waktuSelesai, mataKuliah, topik);
                break;
            case 2:
                System.out.println("Deadline (yyyy-mm-dd): ");
                LocalDate deadline = inputTanggal();
                String prioritas = inputString("Prioritas (Tinggi/Sedang/Rendah): ");
                kegiatan = new KegiatanTugas(0, judul, tanggal, waktuMulai,
                        waktuSelesai, mataKuliah, deadline, prioritas);
                break;
            case 3:
                String ruangan = inputString("Ruangan: ");
                String jenisUjian = inputString("Jenis Ujian (UTS/UAS/Quiz): ");
                String materi = inputString("Materi: ");
                kegiatan = new KegiatanUjian(0, judul, tanggal, waktuMulai,
                        waktuSelesai, mataKuliah, ruangan, jenisUjian, materi);
                break;
        }

        if (kegiatan != null && jadwalManager.tambahKegiatan(kegiatan)) {
            System.out.println("\n✅ Kegiatan berhasil ditambahkan!\n");
        } else {
            System.out.println("\n❌ Gagal menambahkan kegiatan.\n");
        }

        tekanEnter();
    }

    private static void lihatNotifikasi() {
        System.out.println("\n🔔 NOTIFIKASI & REMINDER\n");

        notifikasiManager.cekDeadline();

        if (notifikasiManager.getJumlahNotifikasi() == 0) {
            System.out.println("✅ Tidak ada notifikasi.");
        } else {
            System.out.println("📊 " + notifikasiManager.getSummaryNotifikasi() + "\n");

            for (String notif : notifikasiManager.getNotifikasiAktif()) {
                System.out.println(notif);
            }
        }

        tekanEnter();
    }

    private static void lihatStatistik() {
        System.out.println("\n📊 STATISTIK & ANALISIS\n");
        System.out.println(statistikManager.getSummaryStatistik());

        System.out.println("\n📚 JAM BELAJAR PER MATA KULIAH:");
        var jamPerMK = statistikManager.getJamBelajarPerMataKuliah();
        if (jamPerMK.isEmpty()) {
            System.out.println("  Belum ada data.");
        } else {
            jamPerMK.forEach((mk, jam) ->
                    System.out.println("  - " + mk + ": " + String.format("%.1f jam", jam))
            );
        }

        System.out.println("\n📝 STATUS TUGAS:");
        statistikManager.getStatusTugas().forEach((status, jumlah) ->
                System.out.println("  - " + status + ": " + jumlah)
        );

        tekanEnter();
    }

    private static void cariKegiatan() {
        System.out.println("\n🔍 CARI KEGIATAN\n");
        String keyword = inputString("Masukkan kata kunci: ");

        var hasil = jadwalManager.searchKegiatan(keyword);

        if (hasil.isEmpty()) {
            System.out.println("\n❌ Tidak ditemukan.");
        } else {
            System.out.println("\n✅ Ditemukan " + hasil.size() + " kegiatan:\n");
            for (Kegiatan k : hasil) {
                System.out.println("  - " + k);
            }
        }

        tekanEnter();
    }

    // ========== LOAD DATA SAMPLE ==========
    private static void loadDataSample() {
        // Kegiatan Belajar
        KegiatanBelajar b1 = new KegiatanBelajar(0, "Belajar OOP - Polimorfisme",
                LocalDate.now(), LocalTime.of(8, 0), LocalTime.of(10, 0),
                "Pemrograman Berorientasi Objek", "Polimorfisme & Abstract Class");
        b1.ubahStatus("Selesai");
        jadwalManager.tambahKegiatan(b1);

        KegiatanBelajar b2 = new KegiatanBelajar(0, "Belajar Normalisasi Database",
                LocalDate.now(), LocalTime.of(13, 0), LocalTime.of(15, 0),
                "Basis Data", "1NF, 2NF, 3NF");
        jadwalManager.tambahKegiatan(b2);

        // Tugas
        KegiatanTugas t1 = new KegiatanTugas(0, "Project Aplikasi Jadwal",
                LocalDate.now(), LocalTime.of(19, 0), LocalTime.of(21, 0),
                "Pemrograman Berorientasi Objek", LocalDate.now().plusDays(5), "Tinggi");
        t1.updateProgress(65);
        jadwalManager.tambahKegiatan(t1);

        KegiatanTugas t2 = new KegiatanTugas(0, "ERD Database Perpustakaan",
                LocalDate.now().plusDays(1), LocalTime.of(14, 0), LocalTime.of(16, 0),
                "Basis Data", LocalDate.now().plusDays(3), "Sedang");
        t2.updateProgress(30);
        jadwalManager.tambahKegiatan(t2);

        // Ujian
        KegiatanUjian u1 = new KegiatanUjian(0, "UTS Struktur Data",
                LocalDate.now().plusDays(5), LocalTime.of(8, 0), LocalTime.of(10, 0),
                "Struktur Data", "Lab 301", "UTS", "Tree, Graph, Sorting");
        jadwalManager.tambahKegiatan(u1);
    }

    // ========== HELPER METHODS ==========
    private static String inputString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    private static int inputInteger(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            scanner.next();
            System.out.print("Input tidak valid. " + prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }

    private static LocalDate inputTanggal() {
        String input = scanner.nextLine();
        return LocalDate.parse(input);
    }

    private static LocalTime inputWaktu() {
        String input = scanner.nextLine();
        return LocalTime.parse(input);
    }

    private static void tekanEnter() {
        System.out.print("\nTekan ENTER untuk melanjutkan...");
        scanner.nextLine();
    }
}