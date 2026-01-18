package manager;

import model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JadwalManager (UPDATED)
 * Mengelola semua operasi CRUD untuk kegiatan
 * + AUTO-SAVE setiap ada perubahan data
 */
public class JadwalManager {
    private List<Kegiatan> daftarKegiatan;
    private int nextId;
    private boolean autoSaveEnabled = true;

    // Constructor - AUTO LOAD data
    public JadwalManager() {
        this.daftarKegiatan = new ArrayList<>();
        this.nextId = 1;
        loadData(); // AUTO-LOAD saat startup
    }

    // ============ AUTO SAVE/LOAD ============

    /**
     * Load data dari file saat startup
     */
    public void loadData() {
        List<Kegiatan> loadedData = DataManager.loadData();

        if (!loadedData.isEmpty()) {
            this.daftarKegiatan = loadedData;

            // Update nextId berdasarkan ID tertinggi
            int maxId = loadedData.stream()
                    .mapToInt(Kegiatan::getId)
                    .max()
                    .orElse(0);
            this.nextId = maxId + 1;
        }
    }

    /**
     * Save data ke file (AUTO-SAVE setiap perubahan)
     */
    private void autoSave() {
        if (autoSaveEnabled) {
            DataManager.saveData(daftarKegiatan);
        }
    }

    /**
     * Manual save (bisa dipanggil manual juga)
     */
    public boolean saveData() {
        return DataManager.saveData(daftarKegiatan);
    }

    /**
     * Toggle auto-save
     */
    public void setAutoSave(boolean enabled) {
        this.autoSaveEnabled = enabled;
    }

    /**
     * Export ke CSV
     */
    public boolean exportToCSV(String fileName) {
        return DataManager.exportToCSV(daftarKegiatan, fileName);
    }

    /**
     * Backup data
     */
    public boolean backupData(String backupFileName) {
        return DataManager.backupData(backupFileName);
    }

    // ============ CREATE ============
    public boolean tambahKegiatan(Kegiatan kegiatan) {
        if (kegiatan != null) {
            kegiatan.setId(nextId++);
            daftarKegiatan.add(kegiatan);
            autoSave(); // 🔥 AUTO-SAVE
            return true;
        }
        return false;
    }

    // ============ READ ============

    public List<Kegiatan> getDaftarKegiatan() {
        return new ArrayList<>(daftarKegiatan);
    }

    public Kegiatan getKegiatanById(int id) {
        return daftarKegiatan.stream()
                .filter(k -> k.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Kegiatan> getKegiatanHariIni() {
        LocalDate today = LocalDate.now();
        return daftarKegiatan.stream()
                .filter(k -> k.getTanggal().isEqual(today))
                .collect(Collectors.toList());
    }

    public List<Kegiatan> getKegiatanMingguIni() {
        LocalDate today = LocalDate.now();
        LocalDate endOfWeek = today.plusDays(7);

        return daftarKegiatan.stream()
                .filter(k -> {
                    LocalDate tgl = k.getTanggal();
                    return !tgl.isBefore(today) && !tgl.isAfter(endOfWeek);
                })
                .collect(Collectors.toList());
    }

    public List<Kegiatan> getKegiatanByTanggal(LocalDate tanggal) {
        return daftarKegiatan.stream()
                .filter(k -> k.getTanggal().isEqual(tanggal))
                .collect(Collectors.toList());
    }

    public List<Kegiatan> getKegiatanByMataKuliah(String mataKuliah) {
        return daftarKegiatan.stream()
                .filter(k -> {
                    if (k instanceof KegiatanBelajar) {
                        return ((KegiatanBelajar) k).getMataKuliah().equalsIgnoreCase(mataKuliah);
                    } else if (k instanceof KegiatanTugas) {
                        return ((KegiatanTugas) k).getMataKuliah().equalsIgnoreCase(mataKuliah);
                    } else if (k instanceof KegiatanUjian) {
                        return ((KegiatanUjian) k).getMataKuliah().equalsIgnoreCase(mataKuliah);
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<Kegiatan> getKegiatanByStatus(String status) {
        return daftarKegiatan.stream()
                .filter(k -> k.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public List<Kegiatan> searchKegiatan(String keyword) {
        return daftarKegiatan.stream()
                .filter(k -> k.getJudul().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<KegiatanBelajar> getKegiatanBelajar() {
        return daftarKegiatan.stream()
                .filter(k -> k instanceof KegiatanBelajar)
                .map(k -> (KegiatanBelajar) k)
                .collect(Collectors.toList());
    }

    public List<KegiatanTugas> getKegiatanTugas() {
        return daftarKegiatan.stream()
                .filter(k -> k instanceof KegiatanTugas)
                .map(k -> (KegiatanTugas) k)
                .collect(Collectors.toList());
    }

    public List<KegiatanUjian> getKegiatanUjian() {
        return daftarKegiatan.stream()
                .filter(k -> k instanceof KegiatanUjian)
                .map(k -> (KegiatanUjian) k)
                .collect(Collectors.toList());
    }

    // ============ UPDATE ============

    public boolean editKegiatan(int id, Kegiatan kegiatanBaru) {
        for (int i = 0; i < daftarKegiatan.size(); i++) {
            if (daftarKegiatan.get(i).getId() == id) {
                kegiatanBaru.setId(id);
                daftarKegiatan.set(i, kegiatanBaru);
                autoSave(); // 🔥 AUTO-SAVE
                return true;
            }
        }
        return false;
    }

    public boolean updateStatus(int id, String statusBaru) {
        Kegiatan kegiatan = getKegiatanById(id);
        if (kegiatan != null) {
            kegiatan.ubahStatus(statusBaru);
            autoSave(); // 🔥 AUTO-SAVE
            return true;
        }
        return false;
    }

    // ============ DELETE ============

    public boolean hapusKegiatan(int id) {
        boolean removed = daftarKegiatan.removeIf(k -> k.getId() == id);
        if (removed) {
            autoSave(); // 🔥 AUTO-SAVE
        }
        return removed;
    }

    public void hapusSemuaKegiatan() {
        daftarKegiatan.clear();
        nextId = 1;
        autoSave(); // 🔥 AUTO-SAVE
    }

    // ============ UTILITY ============

    public int getTotalKegiatan() {
        return daftarKegiatan.size();
    }

    public boolean isEmpty() {
        return daftarKegiatan.isEmpty();
    }

    public List<KegiatanTugas> getTugasTerlambat() {
        return getKegiatanTugas().stream()
                .filter(KegiatanTugas::isTerlambat)
                .collect(Collectors.toList());
    }

    public List<KegiatanUjian> getUjianHariIni() {
        return getKegiatanUjian().stream()
                .filter(KegiatanUjian::isUjianHariIni)
                .collect(Collectors.toList());
    }
}