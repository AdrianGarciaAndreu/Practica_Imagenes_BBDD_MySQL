package prueba;


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;


public class ventana {

	/*
	 *  Variables para el diseño gráfico
	 */
	private JFrame Frame = new JFrame();
	private JPanel contentPane;
	private JLabel label = new JLabel("");
	
	/*
	 *  Variables principales de la Base de datos
	 */
	private Connection conexion; //objeto conexion
	private Statement stm; //objeto statement
	private PreparedStatement ps;
	
	/*
	 *  Variables de conexion a la Base de datos
	 */
	private String URL_BD = "jdbc:mysql://localhost/images";  //guarda la URL de la Base de datos.
	private String User_BD = "root"; //guarda el usuario de la base de datos.
	private String Pass_BD = "tonphp"; //guarda la contraseña del usuario de la base de datos.
	
	
	/**
	 * lanza la aplicacion.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ventana frame = new ventana();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	/**
	 * Método para cargar el driver JDBC para MYSQL de acceso a la base de datos
	 */
	public void loadUIManager () {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Método construcor, diseña el programa y lo ejecuta
	 */
	public ventana() {
		
		this.loadUIManager();
		this.LoadDataBaseDriver();
		
		this.Frame.setResizable(false);
		this.Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.Frame.setBounds(100, 100, 566, 452);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.Frame.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
	
		this.label.setBounds(53, 91, 450, 300);
		
		
		JButton LoadImage = new JButton("Cargar Imagen");
		LoadImage.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
			
				JFileChooser jf = new JFileChooser();
				jf.setApproveButtonText("Cargar Imagen");
				jf.showOpenDialog(Frame);
				
				String ruta = null;
				String name = "Prueba";
				
				ruta = jf.getSelectedFile().getAbsolutePath();
				
				LoadImage(ruta,name);

				
			}
		});
		
		
		LoadImage.setBounds(28, 19, 350, 69);
		contentPane.add(LoadImage);
		
	
		contentPane.add(label);
		
		JButton btnNewButton = new JButton("Aplicar Img");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				int index = Integer.parseInt(JOptionPane.showInputDialog(Frame, "Introduce el indice de la imagen: ","Aplicar imagen", 1) );
				
				OpenImage(index,label);
				
			}
		});
		btnNewButton.setBounds(413, 25, 126, 56);
		contentPane.add(btnNewButton);
		label.repaint();
		
		this.Frame.setVisible(true);
	}
	
	

	/**
	 * método que carga el driver de JDBC para MYSQL
	 */
	public void LoadDataBaseDriver() {

		try {

			Class.forName("com.mysql.jdbc.Driver");

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Método para guardar una imagen en la base de datos de la direccion determinada.
	 * 
	 * @param ruta: almacena la ruta (path) del archivo.
	 * @param nombre: almacena un nombre (parametro opcional en la base de datos).
	 */
	public void LoadImage (String ruta,String nombre) {

		try {

			// abre una conexion, a la base de datos determinada con su usuario y contraseña
			this.conexion = DriverManager.getConnection(this.URL_BD, this.User_BD, this.Pass_BD);

			// comando de insercion.
			String comando = "INSERT INTO images.imagenes VALUES (?,?,?)";

			// Abre una conexion (flujo de datos) con el arcchivo (File) determinado
			FileInputStream inputStreamWithFile;

			try {

				// Guarda en la variable "image", una instancia archivo seleccionado previamente en los parametros del metodo.
				File image = new File(ruta); 	

				// instancia el Objeto de tipo FileInputStream con el archivo (File) almacenado en la variable previa.
				inputStreamWithFile = new FileInputStream(image);

				// Instancia una "Sentencia Preparada" (preparedStatement)  desde la conexion y se le pasa como parametro el "comando" 
				this.ps = conexion.prepareStatement(comando);

				/*
				 * Establece los valores en el array interno del comando que se le pasa a la Sentencia.
				 */

				this.ps.setInt(1, 5); // el primer valor "?" se establece como el entero numero 2
				this.ps.setBinaryStream(2, inputStreamWithFile); // el segundo valor "?" se le pasa el flujo que contiene el File

				//this.ps.setString(2, ruta); tambien puede guardarse el archivo pasandole la ruta como String

				this.ps.setString(3, nombre); // el tercer valor "?" se establece como el String almacenado en "nombre


				//ejecuta la sentencia
				this.ps.executeUpdate();
			} 

			catch (FileNotFoundException e) { e.printStackTrace(); }

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		finally {
			
				// cierra la conexion
				try { this.conexion.close(); } 
				catch (SQLException e) {e.printStackTrace(); }
		}

	}
	
	
	
	/**
	 * Método para aplicar una imagen al JFrame, se introduce el indice 
	 * (en la Base de datos) de la imagen a la que hace referencia
	 * y se introduce el Label al cual se le aplica la imagen
	 * @param index
	 * @param label
	 */
	public void OpenImage (int index, JLabel label){

		try {

			this.conexion = DriverManager.getConnection(URL_BD, User_BD, Pass_BD);
			ResultSet  rs;

			try {

				this.stm = conexion.createStatement();
				rs = this.stm.executeQuery("SELECT imagen FROM imagenes WHERE id="+index);

				while ( rs.next() ){

					Blob blob_image = rs.getBlob("imagen");
					byte[] image_data = blob_image.getBytes(1, (int)blob_image.length() );

					//Crea un BufferedImage que lee la imagen (ImageIO), con un nuevo flujo de Array de bytes (el array de bytes image_data).

					/* BufferedImage img = null;					
					 *	img = ImageIO.read(new ByteArrayInputStream(image_data));
					 */

					// Label del JFrame, se le añade como icono directamente el array de bytes "image_data".

					label.setIcon(new ImageIcon(image_data) );

				}


			} catch (SQLException e) {

				e.printStackTrace(); System.out.println("fallo en el SQL");

			}

			/* catch (IOException e2) {

				System.out.println("fallo en la carga de la imagen I/O");
			}*/

		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		finally {

			try {
				this.conexion.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


	}


}





