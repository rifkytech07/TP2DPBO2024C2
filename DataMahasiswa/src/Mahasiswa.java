//Saya Muhammad Rifky Afandi dengan NIM 2202346 mengerjakan TP2 dalam mata kuliah Desain Pemrograman Berbasis Objek untuk keberkahanNya maka
// saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
public class Mahasiswa {
    private String nim;
    private String nama;
    private String jenisKelamin;

    private String programStudi;

    public Mahasiswa(String nim, String nama, String jenisKelamin, String programStudi) {
        this.nim = nim;
        this.nama = nama;
        this.jenisKelamin = jenisKelamin;
        this.programStudi = programStudi;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public void setProgramStudi(String programStudi) {this.programStudi = programStudi; }

    public String getNim() {
        return this.nim;
    }

    public String getNama() {
        return this.nama;
    }

    public String getJenisKelamin() {
        return this.jenisKelamin;
    }

    public String getProgramStudi() {return this.programStudi; }
}
