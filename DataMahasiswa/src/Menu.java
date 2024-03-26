//Saya Muhammad Rifky Afandi dengan NIM 2202346 mengerjakan TP2 dalam mata kuliah Desain Pemrograman Berbasis Objek untuk keberkahanNya maka
// saya tidak melakukan kecurangan seperti yang telah dispesifikasikan. Aamiin.
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Menu extends JFrame{
    public static void main(String[] args) {
        // buat object window
        Menu window = new Menu();

        // atur ukuran window
        window.setSize(400, 560);
        // letakkan window di tengah layar
        window.setLocationRelativeTo(null);
        // isi window
        window.setContentPane(window.mainPanel);
        // ubah warna background
        window.getContentPane().setBackground(Color.white);
        // tampilkan window
        window.setVisible(true);
        // agar program ikut berhenti saat window diclose
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // index baris yang diklik
    private int selectedIndex = -1;
    // list untuk menampung semua mahasiswa
    private ArrayList<Mahasiswa> listMahasiswa;

    private Database database;

    private JPanel mainPanel;
    private JTextField nimField;
    private JTextField namaField;
    private JTable mahasiswaTable;
    private JButton addUpdateButton;
    private JButton cancelButton;
    private JComboBox jenisKelaminComboBox;

    private JComboBox programStudicomboBox;

    private JButton deleteButton;
    private JLabel titleLabel;
    private JLabel nimLabel;
    private JLabel namaLabel;
    private JLabel jenisKelaminLabel;

    private JLabel programStudi;


    // constructor
    public Menu() {
        // inisialisasi listMahasiswa
        listMahasiswa = new ArrayList<>();

        // buat objek database
        database = new Database();
        // isi tabel mahasiswa
        mahasiswaTable.setModel(setTable());

        // ubah styling title
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        // atur isi combo box
        String[] jenisKelaminData = {"", "Laki-Laki", "Perempuan"};
        jenisKelaminComboBox.setModel(new DefaultComboBoxModel(jenisKelaminData));

        // atur isi combo box
        String[] programStudiData = {"", "Matematika", "Fisika", "Kimia", "IPSE", "Ilmu Komputer"};
        programStudicomboBox.setModel(new DefaultComboBoxModel(programStudiData));

        // sembunyikan button delete
        deleteButton.setVisible(false);

        // saat tombol add/update ditekan
        addUpdateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex == -1){
                    insertData();
                } else {
                    updateData();
                }


            }
        });
        // saat tombol delete ditekan
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIndex >= 0){
                    // Tampilkan confirmation prompt
                    int option = JOptionPane.showConfirmDialog(null, "Apakah Anda yakin ingin menghapus data ini?", "Konfirmasi Penghapusan", JOptionPane.YES_NO_OPTION);

                    // Cek jika pengguna menekan tombol Yes
                    if (option == JOptionPane.YES_OPTION) {
                        deleteData(); // Hapus data jika pengguna menekan tombol Yes
                    }
                }
            }
        });
        // saat tombol cancel ditekan
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        // saat salah satu baris tabel ditekan
        mahasiswaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // ubah selectedIndex menjadi baris tabel yang diklik
                selectedIndex = mahasiswaTable.getSelectedRow();

                // simpan value textfield dan combo box
                String selectedNim = mahasiswaTable.getModel().getValueAt(selectedIndex, 1).toString();
                String selectedNama = mahasiswaTable.getModel().getValueAt(selectedIndex, 2).toString();
                String selectedJeniskelamin = mahasiswaTable.getModel().getValueAt(selectedIndex, 3).toString();
                String selectedProgramStudi = mahasiswaTable.getModel().getValueAt(selectedIndex, 4).toString();

                // ubah isi textfield dan combo box
                nimField.setText(selectedNim);
                namaField.setText(selectedNama);
                jenisKelaminComboBox.setSelectedItem(selectedJeniskelamin);
                programStudicomboBox.setSelectedItem(selectedProgramStudi);

                // ubah button "Add" menjadi "Update"
                addUpdateButton.setText("Update");
                // tampilkan button delete
                deleteButton.setVisible(true);
            }
        });
    }

    public final DefaultTableModel setTable() {
        // tentukan kolom tabel
        Object[] column = {"No", "NIM", "Nama", "Jenis Kelamin", "Program Studi"};

        // buat objek tabel dengan kolom yang sudah dibuat
        DefaultTableModel temp = new DefaultTableModel(null, column);

        try {
            ResultSet resultSet = database.selectQuery("SELECT * FROM mahasiswa");
            // isi tabel dengan listMahasiswa
            int i = 0;
            while(resultSet.next()){
                Object[] row = new Object[5];
                row[0] = i + 1;
                row[1] = resultSet.getString("nim");
                row[2] =  resultSet.getString("nama");;
                row[3] =  resultSet.getString("jenis_kelamin");
                row[4] =  resultSet.getString("program_studi");

                temp.addRow(row);
                i++;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return temp; // return juga harus diganti
    }

    public boolean isNimExists(String nim) {
        // Query untuk memeriksa apakah NIM sudah ada di database
        String query = "SELECT COUNT(*) FROM mahasiswa WHERE nim='" + nim + "'";
        ResultSet resultSet = database.selectQuery(query);
        try {
            // Periksa apakah NIM sudah ada
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0; // Return true jika NIM sudah ada, false jika tidak
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false jika terjadi kesalahan atau NIM tidak ditemukan
    }

    public void insertData() {
        // ambil value dari textfield dan combobox
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String programStudi = programStudicomboBox.getSelectedItem().toString();

        // periksa apakah ada input yang kosong
        if (nim.isEmpty() || nama.isEmpty() || jenisKelamin.isEmpty() || programStudi.isEmpty()) {
            // tampilkan dialog error jika ada input yang kosong
            JOptionPane.showMessageDialog(null, "Mohon lengkapi semua field sebelum menambahkan data!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // hentikan operasi insert
        }

        // periksa apakah NIM sudah ada di database
        if (isNimExists(nim)) {
            JOptionPane.showMessageDialog(null, "NIM sudah ada dalam database!", "Error", JOptionPane.ERROR_MESSAGE);
            return; // hentikan operasi insert
        }

        // tambahkan data ke dalam list
        String sql = "INSERT INTO mahasiswa VALUES(null, '" + nim + "', '" + nama + "', '" + jenisKelamin + "', '" + programStudi + "');";
        database.insertUpdateDeleteQuery(sql);

        // update tabel
        mahasiswaTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Insert Berhasil!");
        JOptionPane.showMessageDialog(null, "Data berhasil ditambahkan!");
    }

    public void updateData() {
        // ambil data dari form
        String nim = nimField.getText();
        String nama = namaField.getText();
        String jenisKelamin = jenisKelaminComboBox.getSelectedItem().toString();
        String programStudi = programStudicomboBox.getSelectedItem().toString();

        // periksa apakah ada input yang kosong
        if (nim.isEmpty() || nama.isEmpty() || jenisKelamin.isEmpty() || programStudi.isEmpty()) {
            // tampilkan dialog error jika ada input yang kosong
            JOptionPane.showMessageDialog(null, "Mohon lengkapi semua field sebelum mengubah data!", "Error", JOptionPane.ERROR_MESSAGE);
            // hentikan operasi update
            return;
        }

        // buat query update
        String sql = "UPDATE mahasiswa SET nama='" + nama + "', jenis_kelamin='" + jenisKelamin + "', program_studi='" + programStudi + "' WHERE nim='" + nim + "'";

        // jalankan query update
        database.insertUpdateDeleteQuery(sql);

        // update tabel
        mahasiswaTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Update Berhasil!");
        JOptionPane.showMessageDialog(null, "Data berhasil diubah!");
    }

    public void deleteData() {
        // ambil NIM mahasiswa yang akan dihapus
        String nim = nimField.getText();

        // buat query delete
        String sql = "DELETE FROM mahasiswa WHERE nim='" + nim + "'";

        // jalankan query delete
        database.insertUpdateDeleteQuery(sql);

        // update tabel
        mahasiswaTable.setModel(setTable());

        // bersihkan form
        clearForm();

        // feedback
        System.out.println("Delete Berhasil!");
        JOptionPane.showMessageDialog(null, "Data berhasil dihapus!");
    }


    public void clearForm() {
        // kosongkan semua texfield dan combo box
        nimField.setText("");
        namaField.setText("");
        jenisKelaminComboBox.setSelectedItem("");
        programStudicomboBox.setSelectedItem("");



        // ubah button "Update" menjadi "Add"
        addUpdateButton.setText("Add");
        // sembunyikan button delete
        deleteButton.setVisible(false);
        // ubah selectedIndex menjadi -1 (tidak ada baris yang dipilih)
        selectedIndex = -1;
    }


}
