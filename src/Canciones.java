import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sergi on 24/11/15.
 */
public class Canciones {

    // COMO HACER FUNCIONAR ESTA CLASE:

    //1 -> introducir el nombre de artista
    public static String nombreArtista = "Iron Maiden";

    //2 -> Antes de generar los inserts, hay que saber cuantos albumes hay ya insertados para saber a partir de que número empezar
    public static int numeroDeAlbums = 54;

    //3 -> Introduce el genero del artista
    public static String genero = "heavy metal";

    //4 -> EL valor de id artista va estipulado en el siguiente gráfico:
    public static int idArtista = 4;

    //Grupos:
    //-------------------------
    // Iron Maiden      -> 1
    // Blind Guardian   -> 2
    // Guns N' Roses    -> 3
    // Slash            -> 4
    //                  -> 5
    //                  -> 6
    //                  -> 7
    //                  -> 8
    //                  -> 9
    //                  -> 10

    //5 -> Ejecutar y disfrutar :D

    // Variables de clase
    static String idCancion = "";
    static String apiKey = "&apikey=754f223018be007a45003e3b87877bac";     // Key de Vagalume. Máximo 100.000 peticiones /dia
    static String searchURL = "http://api.vagalume.com.br/search.php?musid=";
    static String urlCancion = "http://api.vagalume.com.br/search.php?musid=" + idCancion;
    static String url = "http://www.vagalume.com.br/" + nombreArtista + "/discografia/index.js";
    static int cont1 = 0;
    static int cont2 = 0;
    static int numeroCanciones = 0;

    public static void main(String[] args) {
        generateInserts(cont1, cont2);
    }

    public static void generateInserts(int conadorAlbumes, int contadorCanciones) {

        // Modificamos el nombre de artista para que la pagina web lo entienda
        nombreArtista = nombreArtista.toLowerCase().replace(" ", "-");
        url = "http://www.vagalume.com.br/" + nombreArtista + "/discografia/index.js";

        JSONObject OBJETO_ENTERO = (JSONObject) JSONValue.parse(getJSON(url));
        JSONObject DISCOGRAPHY = (JSONObject) JSONValue.parse(OBJETO_ENTERO.get("discography").toString());
        JSONArray ARRAY_ALBUMS = (JSONArray) JSONValue.parse(DISCOGRAPHY.get("item").toString());

        // Canciones

        int numeroDeAlbums = 54;

        String insertsAlbumes =
                "-- -----------------------------------------------------\n" +
                        "-- Table `FeatherLyricsBBDD`.`Albumes`\n" +
                        "-- -----------------------------------------------------\n";

        String insertsCanciones =
                "-- -----------------------------------------------------\n" +
                        "-- Table `FeatherLyricsBBDD`.`canciones`\n" +
                        "-- -----------------------------------------------------\n\n";

        try {


            for (int iteradorAlbumes = conadorAlbumes; iteradorAlbumes < ARRAY_ALBUMS.size(); iteradorAlbumes++) {
                JSONObject album = (JSONObject) JSONValue.parse(ARRAY_ALBUMS.get(iteradorAlbumes).toString());

                String nombreAlbum = album.get("desc").toString();
                String anyoAlbum = album.get("published").toString();

                JSONArray ENESTEJSONHAYUNARRAYSINSENTIDO = (JSONArray) JSONValue.parse(album.get("discs").toString());
                JSONArray ARRAY_DISCS = (JSONArray) JSONValue.parse(ENESTEJSONHAYUNARRAYSINSENTIDO.get(0).toString());

                insertsAlbumes = insertsAlbumes + "\n";

                insertsAlbumes =
                        "INSERT INTO albumes(id_Album, Nombre_Album, Anyo_Album, Duracion_Album, Grupo_id, Genero_Nombre)\n" +
                        "VALUES ('" + (numeroDeAlbums + iteradorAlbumes + 1) + "', '" + nombreAlbum + "', '" + anyoAlbum + "', '58:31', '" + idArtista + "', '" + genero + "');" + "\n";


                System.out.println(insertsAlbumes);
                cont1 = iteradorAlbumes;

                for (int iteradorCanciones = contadorCanciones; iteradorCanciones < ARRAY_DISCS.size(); iteradorCanciones++) {

                    System.out.println("------------------------------------------");
                    System.out.println("Numero de albumes " + cont1);
                    System.out.println("Numero de canciones del album " + cont2);
                    System.out.println("Numero de canciones totales: " + numeroCanciones);
                    System.out.println("------------------------------------------");

                    // Sacamos la id de cada cancion
                    JSONObject SELECTED_DISC = (JSONObject) JSONValue.parse(ARRAY_DISCS.get(iteradorCanciones).toString());
                    idCancion = SELECTED_DISC.get("id").toString();

                    urlCancion = searchURL + idCancion + apiKey;
                    System.out.println(urlCancion);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Canciones
                    JSONObject objeto = (JSONObject) JSONValue.parse(getJSON(urlCancion));
                    JSONArray mus = (JSONArray) JSONValue.parse(objeto.get("mus").toString());
                    JSONObject arraySinSentido = (JSONObject) JSONValue.parse(mus.get(0).toString());
                    String letraCancion = arraySinSentido.get("text").toString();
                    System.out.println(letraCancion);

                    cont2 = iteradorCanciones;
                    numeroCanciones++;
                }
            }
            // System.out.println(insertsAlbumes);
        } catch (NullPointerException a) {
            cont2 = 0;
            cont1++;
            generateInserts(cont1, cont2);
            System.out.println("------------------");
            System.out.println(a);
            System.out.println("------------------");
        }
    }

    public static String getJSON(String URLtoRead) {

        try {
            StringBuilder stringJSON = new StringBuilder();
            URL url = new URL(URLtoRead);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;

            while ((line = reader.readLine()) != null) {
                stringJSON.append(line);
            }

            reader.close();
            return stringJSON.toString();
        } catch (Exception one) {
            return "getJSON() didn't work, you are in the Catch block!";
        }
    }

    public static String formateoComillas (String frase){

        // Las comillas y algunos caracteres que pueden hacer petar el programa. Lo paliamos con este método.

        String comillas = "\'";

        if (frase.contains(comillas)) {
            frase = frase.replace(comillas,"\"");
        }
        return frase;
    }

    public static void crea(File fitxer) {

        if (fitxer.exists()) {
            fitxer.delete();
        }

        System.out.println("----------------------------------------------------------");
        System.out.println("El arxiu especificat no existeix, es creara automaticament");

        try {
            // A partir del objecte file creem el fitxer fisicament
            if (fitxer.createNewFile()) {
                System.out.println("El fitxer file s'ha creat correctament");
            } else {
                System.out.println("N'ho s'ha pogut crear el fitxer");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        System.out.println("---------------------------------------------");
    }
}