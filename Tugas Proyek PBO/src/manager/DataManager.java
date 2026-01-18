package manager;

import model.*;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DataManager - Mengelola Save & Load Data
 * Format: Custom TXT (Simple & Readable)
 */
public class DataManager {
    private static final String DATA_FILE = "jadwal_data.txt";
    private static final String SEPARATOR = "|";

    /**
     * SAVE - Simpan semua kegiatan ke file
     */
    public static boolean saveData(List<Kegiatan> daftarKegiatan) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {

            for (Kegiatan k : daftarKegiatan) {
                StringBuilder line = new StringBuilder();

                // Common fields
                line.append(k.getId()).append(SEPARATOR);
                line.append(getKegiatanType(k)).append(SEPARATOR);
                line.append(k.getJudul()).append(SEPARATOR);
                line.append(k.getTanggal()).append(SEPARATOR);
                line.append(k.getWaktuMulai()).append(SEPARATOR);
                line.append(k.getWaktuSelesai()).append(SEPARATOR);
                line.append(k.getStatus()).append(SEPARATOR);

                // Type-specific fields
                if (k instanceof KegiatanBelajar) {
                    KegiatanBelajar kb = (KegiatanBelajar) k;
                    line.append(kb.getMataKuliah()).append(SEPARATOR);
                    line.append(kb.getTopik());

                } else if (k instanceof KegiatanTugas) {
                    KegiatanTugas kt = (KegiatanTugas) k;
                    line.append(kt.getMataKuliah()).append(SEPARATOR);
                    line.append(kt.getDeadline()).append(SEPARATOR);
                    line.append(kt.getPrioritas()).append(SEPARATOR);
                    line.append(kt.getProgress());

                } else if (k instanceof KegiatanUjian) {
                    KegiatanUjian ku = (KegiatanUjian) k;
                    line.append(ku.getMataKuliah()).append(SEPARATOR);
                    line.append(ku.getRuangan()).append(SEPARATOR);
                    line.append(ku.getJenisUjian()).append(SEPARATOR);
                    line.append(ku.getMateriUjian());
                }

                writer.write(line.toString());
                writer.newLine();
            }

            System.out.println("✅ Data berhasil disimpan ke " + DATA_FILE);
            return true;

        } catch (IOException e) {
            System.err.println("❌ Error saat menyimpan data: " + e.getMessage());
            return false;
        }
    }

    /**
     * LOAD - Muat data dari file
     */
    public static List<Kegiatan> loadData() {
        List<Kegiatan> daftarKegiatan = new ArrayList<>();
        File file = new File(DATA_FILE);

        if (!file.exists()) {
            System.out.println("ℹ️  File data tidak ditemukan. Mulai dengan data kosong.");
            return daftarKegiatan;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int loadedCount = 0;

            while ((line = reader.readLine()) != null) {
                try {
                    Kegiatan kegiatan = parseLine(line);
                    if (kegiatan != null) {
                        daftarKegiatan.add(kegiatan);
                        loadedCount++;
                    }
                } catch (Exception e) {
                    System.err.println("⚠️  Skip baris yang rusak: " + line);
                }
            }

            System.out.println("✅ Berhasil memuat " + loadedCount + " kegiatan dari " + DATA_FILE);

        } catch (IOException e) {
            System.err.println("❌ Error saat memuat data: " + e.getMessage());
        }

        return daftarKegiatan;
    }

    /**
     * Parse satu baris data menjadi objek Kegiatan
     */
    private static Kegiatan parseLine(String line) {
        String[] parts = line.split("\\" + SEPARATOR);

        if (parts.length < 7) {
            throw new IllegalArgumentException("Format data tidak valid");
        }

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String judul = parts[2];
        LocalDate tanggal = LocalDate.parse(parts[3]);
        LocalTime waktuMulai = LocalTime.parse(parts[4]);
        LocalTime waktuSelesai = LocalTime.parse(parts[5]);
        String status = parts[6];

        Kegiatan kegiatan = null;

        switch (type) {
            case "KegiatanBelajar":
                if (parts.length >= 9) {
                    String mataKuliah = parts[7];
                    String topik = parts[8];
                    kegiatan = new KegiatanBelajar(id, judul, tanggal, waktuMulai,
                            waktuSelesai, mataKuliah, topik);
                }
                break;

            case "KegiatanTugas":
                if (parts.length >= 11) {
                    String mataKuliah = parts[7];
                    LocalDate deadline = LocalDate.parse(parts[8]);
                    String prioritas = parts[9];
                    int progress = Integer.parseInt(parts[10]);

                    KegiatanTugas kt = new KegiatanTugas(id, judul, tanggal, waktuMulai,
                            waktuSelesai, mataKuliah, deadline, prioritas);
                    kt.updateProgress(progress);
                    kegiatan = kt;
                }
                break;

            case "KegiatanUjian":
                if (parts.length >= 11) {
                    String mataKuliah = parts[7];
                    String ruangan = parts[8];
                    String jenisUjian = parts[9];
                    String materiUjian = parts[10];

                    kegiatan = new KegiatanUjian(id, judul, tanggal, waktuMulai,
                            waktuSelesai, mataKuliah, ruangan,
                            jenisUjian, materiUjian);
                }
                break;
        }

        if (kegiatan != null) {
            kegiatan.ubahStatus(status);
        }

        return kegiatan;
    }

    /**
     * Dapatkan tipe kegiatan (class name)
     */
    private static String getKegiatanType(Kegiatan k) {
        if (k instanceof KegiatanBelajar) return "KegiatanBelajar";
        if (k instanceof KegiatanTugas) return "KegiatanTugas";
        if (k instanceof KegiatanUjian) return "KegiatanUjian";
        return "Unknown";
    }

    /**
     * Cek apakah file data ada
     */
    public static boolean dataFileExists() {
        return new File(DATA_FILE).exists();
    }

    /**
     * Hapus file data (reset)
     */
    public static boolean clearData() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    /**
     * Backup data ke file lain
     */
    public static boolean backupData(String backupFileName) {
        try {
            File source = new File(DATA_FILE);
            File dest = new File(backupFileName);

            if (!source.exists()) {
                return false;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(source));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(dest))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    writer.write(line);
                    writer.newLine();
                }
            }

            System.out.println("✅ Backup berhasil ke " + backupFileName);
            return true;

        } catch (IOException e) {
            System.err.println("❌ Error saat backup: " + e.getMessage());
            return false;
        }
    }

    /**
     * Export ke format CSV
     */
    public static boolean exportToCSV(List<Kegiatan> daftarKegiatan, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {

            // Header CSV
            writer.write("ID,Tipe,Judul,Tanggal,Waktu Mulai,Waktu Selesai,Status,Mata Kuliah,Detail");
            writer.newLine();

            for (Kegiatan k : daftarKegiatan) {
                StringBuilder line = new StringBuilder();
                line.append(k.getId()).append(",");
                line.append(getKegiatanType(k)).append(",");
                line.append("\"").append(k.getJudul()).append("\",");
                line.append(k.getTanggal()).append(",");
                line.append(k.getWaktuMulai()).append(",");
                line.append(k.getWaktuSelesai()).append(",");
                line.append(k.getStatus()).append(",");

                if (k instanceof KegiatanBelajar) {
                    KegiatanBelajar kb = (KegiatanBelajar) k;
                    line.append("\"").append(kb.getMataKuliah()).append("\",");
                    line.append("\"").append(kb.getTopik()).append("\"");
                } else if (k instanceof KegiatanTugas) {
                    KegiatanTugas kt = (KegiatanTugas) k;
                    line.append("\"").append(kt.getMataKuliah()).append("\",");
                    line.append("Deadline: ").append(kt.getDeadline())
                            .append(" | Progress: ").append(kt.getProgress()).append("%");
                } else if (k instanceof KegiatanUjian) {
                    KegiatanUjian ku = (KegiatanUjian) k;
                    line.append("\"").append(ku.getMataKuliah()).append("\",");
                    line.append("\"").append(ku.getRuangan()).append(" | ")
                            .append(ku.getJenisUjian()).append("\"");
                }

                writer.write(line.toString());
                writer.newLine();
            }

            System.out.println("✅ Export CSV berhasil ke " + fileName);
            return true;

        } catch (IOException e) {
            System.err.println("❌ Error saat export CSV: " + e.getMessage());
            return false;
        }
    }
}