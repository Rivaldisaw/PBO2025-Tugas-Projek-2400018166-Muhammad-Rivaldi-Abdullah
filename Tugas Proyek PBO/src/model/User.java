package model;

/**
 * Class User
 * Menerapkan ENKAPSULASI (atribut private + getter/setter)
 */
public class User {
    // Atribut PRIVATE - Enkapsulasi
    private String nim;
    private String nama;
    private int semester;
    private String programStudi;

    // Constructor
    public User(String nim, String nama, int semester, String programStudi) {
        this.nim = nim;
        this.nama = nama;
        this.semester = semester;
        this.programStudi = programStudi;
    }

    // Getter dan Setter - Enkapsulasi
    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        if (semester > 0 && semester <= 14) { // Validasi semester
            this.semester = semester;
        }
    }

    public String getProgramStudi() {
        return programStudi;
    }

    public void setProgramStudi(String programStudi) {
        this.programStudi = programStudi;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (Semester %d)", nim, nama, semester);
    }
}