package com.utfpr.amiltonjr.revisaoautomotiva;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
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

public class VeiculosActivity extends AppCompatActivity {

    private ListView           listViewVeiculos;
    private ArrayAdapter<Veiculo> listaAdapter;

    private static final int REQUEST_NOVO_VEICULO    = 1;
    private static final int REQUEST_ALTERAR_VEICULO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        listViewVeiculos = (ListView) findViewById(R.id.listViewItens);

        listViewVeiculos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Veiculo veiculo = (Veiculo) parent.getItemAtPosition(position);

                VeiculoActivity.alterar(VeiculosActivity.this, REQUEST_ALTERAR_VEICULO, veiculo);
            }
        });

        popularLista();

        registerForContextMenu(listViewVeiculos);

        setTitle(R.string.veiculos);
    }

    private void popularLista(){

        TextView textSemManutencoes = (TextView) findViewById(R.id.textSemManutencoes);
        Button btnCadastrar = (Button) findViewById(R.id.btnCadastrar);
        ListView listViewItens = (ListView) findViewById(R.id.listViewItens);

        if (!temVeiculos()) {

            textSemManutencoes.setVisibility(View.VISIBLE);
            textSemManutencoes.setText(R.string.sem_veiculos);
            btnCadastrar.setVisibility(View.VISIBLE);
            btnCadastrar.setText(R.string.novo_veiculo);
            listViewItens.setVisibility(View.INVISIBLE);

            // Listener do botão Cadastrar
            btnCadastrar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    novoVeiculo();
                }
            });

            return;
        }

        textSemManutencoes.setVisibility(View.INVISIBLE);
        btnCadastrar.setVisibility(View.INVISIBLE);
        listViewItens.setVisibility(View.VISIBLE);

        List<Veiculo> lista = null;

        try {
            ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);

            lista = conexao.getVeiculoDao().queryBuilder().orderBy("descricao", true).query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaAdapter = new ArrayAdapter<Veiculo>(this, android.R.layout.simple_list_item_1, lista);

        listViewVeiculos.setAdapter(listaAdapter);
    }

    private void novoVeiculo() {
        VeiculoActivity.novo(this, REQUEST_NOVO_VEICULO);
    }

    private void excluirVeiculo(final Veiculo veiculo){

        try {

            ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);
            List<Manutencao> lista = conexao.getManutencaoDao().queryBuilder().where().eq("veiculo_id", veiculo.getId()).query();

            if (lista != null && lista.size() > 0){
                UtilsGUI.avisoErro(this, R.string.veiculo_usado);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String mensagem = getString(R.string.deseja_realmente_apagar) + "\n" + veiculo.getPlaca();

        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch(which){
                            case DialogInterface.BUTTON_POSITIVE:

                                try {
                                    ConexaoDatabase conexao = ConexaoDatabase.getInstance(VeiculosActivity.this);

                                    conexao.getVeiculoDao().delete(veiculo);

                                    listaAdapter.remove(veiculo);

                                    popularLista();

                                } catch (Exception e) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //if ((requestCode == REQUEST_NOVO_VEICULO || requestCode == REQUEST_ALTERAR_VEICULO) && resultCode == Activity.RESULT_OK) {
            popularLista();
        //}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lista_veiculos, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemNovo:
                novoVeiculo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

        Veiculo veiculo = (Veiculo) listViewVeiculos.getItemAtPosition(info.position);

        switch(item.getItemId()){

            case R.id.menuItemAbrir:
                VeiculoActivity.alterar(this, REQUEST_ALTERAR_VEICULO, veiculo);
                return true;

            case R.id.menuItemApagar:
                excluirVeiculo(veiculo);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
}
