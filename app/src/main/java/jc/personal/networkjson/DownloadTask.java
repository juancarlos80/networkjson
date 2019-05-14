package jc.personal.networkjson;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadTask extends AsyncTask<String, Integer, Result> {

    private DownloadRetorno<String> retorno;

    DownloadTask( DownloadRetorno<String> retorno){
        setRetorno( retorno );
    }

    void setRetorno( DownloadRetorno<String> mRetorno ){
        retorno = mRetorno;
    }

    @Override
    public void onProgressUpdate(Integer... progress){
        Integer progressCode = progress[0];
        switch ( progressCode ){
            case DownloadRetorno.Progreso.ERROR:
                retorno.updateFromDownload("Error al descargar el contenido");
                break;
            case DownloadRetorno.Progreso.CONNECT_SUCCESS:
                retorno.updateProgreso("Conexión Existosa");
                break;
            case DownloadRetorno.Progreso.GET_INPUT_STREAM_SUCCESS:
                retorno.updateProgreso("Flujo de entrada abierto");
                break;
            case DownloadRetorno.Progreso.PROCESS_INPUT_STREAM_IN_PROGESS:
                retorno.updateProgreso("Lectura el proceso");
                break;
            case DownloadRetorno.Progreso.PROCESS_INPUT_STREAM_SUCCESS:
                retorno.updateProgreso("Lectura terminó exitosamente");
                break;
        }
    }

    @Override
    protected void onPreExecute(){
        if( retorno != null ){
            NetworkInfo networkInfo = retorno.getActiveNetworkInfo();

            if( networkInfo == null || !networkInfo.isConnected() ||
                    ( networkInfo.getType() != ConnectivityManager.TYPE_WIFI ) &&
                            networkInfo.getType() != ConnectivityManager.TYPE_MOBILE){


                retorno.updateFromDownload( "No se tiene conexión a Internet" );
                cancel( true );
            }
        }
    }

    @Override
    protected Result doInBackground( String... urls ){
        Result result = null;

        if( !isCancelled() && urls != null && urls.length > 0 ){
            String urlString = urls[0];

            try{
                URL url = new URL(urlString);
                String resultString = downloadUrl(url);

                if( resultString != null ){
                    result = new Result( resultString );
                } else {
                    throw new IOException("No se tuvo una respuesta");
                }

            } catch ( Exception e ){
                result = new Result(e);
            }
        }
        return result;
    }

    @Override
    protected void onPostExecute(Result result){
        if( result.mResultValue != null ) {
            retorno.finishDownloading("Terminado con Exito, lectura" );
            retorno.updateFromDownload(result.mResultValue);
        }
        if( result.mException != null ){
            retorno.updateProgreso("Terminado con Error");
            retorno.updateFromDownload( result.mException.getMessage() );
        }
    }

    @Override
    protected void onCancelled( Result result ){

    }

    private String downloadUrl( URL url ) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try{
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestMethod("GET");
            connection.setDoInput( true );

            connection.connect();
            publishProgress( DownloadRetorno.Progreso.CONNECT_SUCCESS );

            int responseCode = connection.getResponseCode();
            if( responseCode != HttpURLConnection.HTTP_OK ){
                throw  new IOException( "HTTP error code: "+responseCode );
            }

            stream = connection.getInputStream();
            publishProgress( DownloadRetorno.Progreso.GET_INPUT_STREAM_SUCCESS );

            if( stream != null ){
                publishProgress( DownloadRetorno.Progreso.PROCESS_INPUT_STREAM_IN_PROGESS );
                result = readStream( stream );
            }

            publishProgress( DownloadRetorno.Progreso.PROCESS_INPUT_STREAM_SUCCESS );
        } finally {
            if( stream != null ) {
                stream.close();
            }
            if( connection != null ){
                connection.disconnect();
            }
        }
        return result;
    }

    public String readStream(InputStream stream) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader( stream, "UTF-8" );
        int bufferSize = 5000;
        char[] rawBuffer = new char[ bufferSize ];
        int readSize;

        StringBuffer buffer = new StringBuffer();
        while ( ( (readSize = reader.read(rawBuffer)) != -1)  ){
            if( readSize < bufferSize ){
                bufferSize = readSize;
            }
            buffer.append( rawBuffer, 0, bufferSize );
        }
        return buffer.toString();
    }
}
