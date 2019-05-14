package jc.personal.networkjson;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements DownloadRetorno<String> {

    Spinner spi_metodo;
    EditText edi_url;
    EditText tex_resultado;
    TextView tex_progreso;

    DownloadTask downloadTask;
    DownloadRetorno<String> retorno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spi_metodo = (Spinner) findViewById(R.id.spinn_metodo);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.metodos_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spi_metodo.setAdapter(adapter);

        edi_url = (EditText) findViewById(R.id.edit_url);
        tex_resultado = (EditText) findViewById(R.id.text_resultado);
        tex_progreso = (TextView) findViewById(R.id.text_estatus);

        retorno = (DownloadRetorno<String>) this;

    }

    @Override
    public void updateFromDownload(String result) {
        tex_resultado.setText( result );
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connecManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connecManager.getActiveNetworkInfo();
        return networkInfo;
    }
    
    @Override
    public void finishDownloading(String mensaje) {
        updateProgreso(mensaje);
    }

    public void obtener_url(View btn){
        downloadTask = new DownloadTask( retorno );
        downloadTask.execute( edi_url.getText().toString() );
    }

    @Override
    public void updateProgreso(String result) {
        tex_progreso.setText( result );
    }
}
