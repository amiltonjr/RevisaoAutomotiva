package com.utfpr.amiltonjr.revisaoautomotiva;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.utfpr.amiltonjr.revisaoautomotiva.modelo.Manutencao;
import com.utfpr.amiltonjr.revisaoautomotiva.modelo.Veiculo;
import com.utfpr.amiltonjr.revisaoautomotiva.persistencia.ConexaoDatabase;
import com.utfpr.amiltonjr.revisaoautomotiva.utils.UtilsGUI;

import java.sql.SQLException;
import java.util.List;

public class ManutencoesActivity extends AppCompatActivity {

    private ListView             listViewManutencao;
    private ArrayAdapter<Manutencao> listaAdapter;

    private static final int REQUEST_NOVA_MANUTENCAO    = 1;
    private static final int REQUEST_ALTERAR_MANUTENCAO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        listViewManutencao = (ListView) findViewById(R.id.listViewItens);

        listViewManutencao.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Manutencao manutencao = (Manutencao) parent.getItemAtPosition(position);

                ManutencaoActivity.alterar(ManutencoesActivity.this, REQUEST_ALTERAR_MANUTENCAO, manutencao);
            }
        });

        registerForContextMenu(listViewManutencao);
    }

    @Override
    protected void onResume() {
        super.onResume();

        popularLista();
    }

    private void popularLista() {

        TextView textSemManutencoes = (TextView) findViewById(R.id.textSemManutencoes);
        Button btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        ListView listViewItens = (ListView) findViewById(R.id.listViewItens);

        if (!temManutencoes()) {
            textSemManutencoes.setVisibility(View.VISIBLE);
            btnCadastrar.setVisibility(View.VISIBLE);
            listViewItens.setVisibility(View.INVISIBLE);

            // Listener do botão Cadastrar
            btnCadastrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    novaManutencao();
                }
            });

            return;
        }

        textSemManutencoes.setVisibility(View.INVISIBLE);
        btnCadastrar.setVisibility(View.INVISIBLE);
        listViewItens.setVisibility(View.VISIBLE);

        List<Manutencao> lista = null;

        try {
            ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);

            lista = conexao.getManutencaoDao().queryBuilder().orderBy("descricao", true).query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaAdapter = new ArrayAdapter<Manutencao>(this, android.R.layout.simple_list_item_1, lista);

        listViewManutencao.setAdapter(listaAdapter);
    }

    private void listaVeiculos() {

        Intent intent = new Intent(this, VeiculosActivity.class);

        startActivity(intent);
    }

    private void excluirManutencao(final Manutencao manutencao){

        String mensagem = getString(R.string.deseja_realmente_apagar) + "\n" + manutencao.getDescricao();

        DialogInterface.OnClickListener listener =
            new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialog, int which) {

                   switch(which){
                       case DialogInterface.BUTTON_POSITIVE:

                           try {
                               ConexaoDatabase conexao = ConexaoDatabase.getInstance(ManutencoesActivity.this);

                               conexao.getManutencaoDao().delete(manutencao);

                               listaAdapter.remove(manutencao);

                               popularLista();

                           } catch (SQLException e) {
                               e.printStackTrace();
                           }

                           break;
                       case DialogInterface.BUTTON_NEGATIVE:

                           break;
                   }
               }
            };

        UtilsGUI.confirmaAcao(this, mensagem, listener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //if ((requestCode == REQUEST_NOVA_MANUTENCAO || requestCode == REQUEST_ALTERAR_MANUTENCAO) && resultCode == Activity.RESULT_OK) {
            popularLista();
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_manutencoes, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.menuItemNovo:
                // Se não há veículos cadastrados no banco de dados
                if (!temVeiculos()) {
                    UtilsGUI.avisoErro(this, R.string.sem_veiculos);
                    return false;
                }

                novaManutencao();

                return true;

            case R.id.menuItemVeiculos:
                listaVeiculos();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void novaManutencao() {
        ManutencaoActivity.nova(this, REQUEST_NOVA_MANUTENCAO);
    }

    private boolean temManutencoes() {

        List<Manutencao> lista = null;

        try {
            ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);

            lista = conexao.getManutencaoDao().queryBuilder().orderBy("descricao", true).query();

            return !lista.isEmpty();

        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }

    private boolean temVeiculos() {

        List<Veiculo> lista = null;

        try {
            ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);

            lista = conexao.getVeiculoDao().queryBuilder().orderBy("placa", true).query();

            return !lista.isEmpty();

        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        getMenuInflater().inflate(R.menu.item_selecionado, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info;

        info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Manutencao manutencao = (Manutencao) listViewManutencao.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemAbrir:
                ManutencaoActivity.alterar(this, REQUEST_ALTERAR_MANUTENCAO, manutencao);
                return true;

            case R.id.menuItemApagar:
                excluirManutencao(manutencao);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
