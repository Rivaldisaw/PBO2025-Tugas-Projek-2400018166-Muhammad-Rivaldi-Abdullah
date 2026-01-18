package model;

/**
 * Class MataKuliah
 * Menerapkan ENKAPSULASI (atribut private + getter/setter)
 */
public class MataKuliah {
    // Atribut PRIVATE - Enkapsulasi
    private String kodeMK;
    private String namaMK;
    private int sks;
    private String dosen;
    private String ruangan;

    // Constructor
    public MataKuliah(String kodeMK, String namaMK, int sks,
                      String dosen, String ruangan) {
        this.kodeMK = kodeMK;
        this.namaMK = namaMK;
        this.sks = sks;
        this.dosen = dosen;
        this.ruangan = ruangan;
    }

    // Getter dan Setter - Enkapsulasi
    // Akses ke atribut private hanya melalui method ini
    public String getKodeMK() {
        return kodeMK;
    }

    public void setKodeMK(String kodeMK) {
        this.kodeMK = kodeMK;
    }

    public String getNamaMK() {
        return namaMK;
    }

    public void setNamaMK(String namaMK) {
        this.namaMK = namaMK;
    }

    public int getSks() {
        return sks;
    }

    public void setSks(int sks) {
        if (sks > 0 && sks <= 6) { // Validasi SKS
            this.sks = sks;
        }
    }

    public String getDosen() {
        return dosen;
    }

    public void setDosen(String dosen) {
        this.dosen = dosen;
    }

    public String getRuangan() {
        return ruangan;
    }

    public void setRuangan(String ruangan) {
        this.ruangan = ruangan;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%d SKS)", kodeMK, namaMK, sks);
    }
}