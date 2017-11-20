package com.utfpr.amiltonjr.revisaoautomotiva;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.utfpr.amiltonjr.revisaoautomotiva.modelo.Manutencao;
import com.utfpr.amiltonjr.revisaoautomotiva.modelo.Veiculo;
import com.utfpr.amiltonjr.revisaoautomotiva.persistencia.ConexaoDatabase;
import com.utfpr.amiltonjr.revisaoautomotiva.utils.UtilsGUI;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ManutencaoActivity extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private static final int REQUEST_NOVO_VEICULO = 1;
    private static final String SAVENAME = "PREFERENCIAS";

    private EditText    editTextDescricao;
    private Spinner     spinnerTipo;
    private EditText    editTextQuilometragem;
    private EditText    editTextValor;
    private Spinner     spinnerVeiculo;

    private List<Veiculo> listaVeiculos = null;
    private int posicao = -1;
    private int posicao2 = -1;

    private int modo;
    private Manutencao manutencao;
    private boolean editado = false;

    public static void nova(Activity activity, int requestCode){

        Intent intent = new Intent(activity, ManutencaoActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, NOVO);
    }

    public static void alterar(Activity activity, int requestCode, Manutencao manutencao) {

        Intent intent = new Intent(activity, ManutencaoActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, manutencao.getId());

        activity.startActivityForResult(intent, ALTERAR);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manutencao);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextDescricao       = (EditText) findViewById(R.id.editTextDescricao);
        spinnerTipo             = (Spinner) findViewById(R.id.spinnerTipoRevisao);
        editTextQuilometragem   = (EditText) findViewById(R.id.editTextQuilometragem);
        editTextValor           = (EditText) findViewById(R.id.editTextValor);
        spinnerVeiculo          = (Spinner) findViewById(R.id.spinnerVeiculo);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO);

        if (modo == ALTERAR) {

            int id = bundle.getInt(ID);

            try {
                ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);

                manutencao = conexao.getManutencaoDao().queryForId(id);

                editTextDescricao.setText(manutencao.getDescricao());
                editTextQuilometragem.setText(String.valueOf(manutencao.getQuilometragem()));
                editTextValor.setText(String.valueOf(manutencao.getValor()));

                posicao2 = posicaoTipo(manutencao.getTipo());
                spinnerTipo.setSelection(posicao2);

                conexao.getVeiculoDao().refresh(manutencao.getVeiculo());

                if (listaVeiculos == null)
                    popularSpinner();

                posicao = posicaoVeiculo(manutencao.getVeiculo());
                spinnerVeiculo.setSelection(posicao);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            setTitle(R.string.alterar_manutencao);

        } else {

            posicao = -1;

            manutencao = new Manutencao();

            setTitle(R.string.nova_manutencao);
        }
        
        // Listener da descrição
        editTextDescricao.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editado) {
                    editado = true;

                    findViewById(R.id.btnSalvar).setEnabled(true);
                }
            }
        });

        // Listener do tipo
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int chamadas = 0;

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chamadas++;

                if ((modo == NOVO && chamadas > 1 && !editado) || (modo == ALTERAR && i != posicao2 && !editado)) {
                    editado = true;

                    findViewById(R.id.btnSalvar).setEnabled(true);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                posicao2 = -1;
            }
        });

        // Listener da quilometragem
        editTextQuilometragem.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editado) {
                    editado = true;

                    findViewById(R.id.btnSalvar).setEnabled(true);
                }
            }
        });

        // Listener do valor
        editTextValor.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editado) {
                    editado = true;

                    findViewById(R.id.btnSalvar).setEnabled(true);
                }
            }
        });

        // Listener do veículo
        spinnerVeiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int chamadas = 0;

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                chamadas++;

                if ((modo == NOVO && chamadas > 1 && !editado) || (modo == ALTERAR && i != posicao && !editado)) {
                    editado = true;

                    findViewById(R.id.btnSalvar).setEnabled(true);
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                posicao = -1;
            }
        });

        // Listener do adicionar veículo
        findViewById(R.id.btnAddVeiculo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                novoVeiculo();
            }
        });

        // Listener do botão Salvar
        findViewById(R.id.btnSalvar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvar();
            }
        });

        // Listener do botão Voltar
        findViewById(R.id.btnVoltar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelar();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        popularSpinner();
    }

    private int posicaoTipo(String tipo) {

        List<String> listaTipos = Arrays.asList(getResources().getStringArray(R.array.tipos_manutencao));

        try {
            for (int pos = 0; pos < listaTipos.size(); pos++) {

                String t = listaTipos.get(pos);

                if (t.equals(tipo))
                    return pos;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();

            return -1;
        }

        return -1;
    }

    private int posicaoVeiculo(Veiculo veiculo) {

        try {
            for (int pos = 0; pos < listaVeiculos.size(); pos++) {

                Veiculo t = listaVeiculos.get(pos);

                if (t.getId() == veiculo.getId())
                    return pos;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();

            return -1;
        }

        return -1;
    }

    private void popularSpinner() {

        listaVeiculos = null;

        try {
            ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);

            listaVeiculos = conexao.getVeiculoDao().queryBuilder().orderBy("placa", true).query();

            ArrayAdapter<Veiculo> spinnerAdapter = new ArrayAdapter<Veiculo>(this, android.R.layout.simple_list_item_1, listaVeiculos);

            spinnerVeiculo.setAdapter(spinnerAdapter);

            if (posicao > -1)
                spinnerVeiculo.setSelection(posicao);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    private void salvar() {

        String descricao  = UtilsGUI.validaCampoTexto(this, editTextDescricao, R.string.descricao_vazia);
        String txtQuilometragem = UtilsGUI.validaCampoTexto(this, editTextQuilometragem, R.string.quilometragem_vazia);
        int quilometragem = Integer.parseInt(txtQuilometragem);
        double valor = Double.parseDouble(UtilsGUI.validaCampoTexto(this, editTextValor, R.string.valor_vazio));

        if (descricao == null || txtQuilometragem == null) {
            return;
        }

        // Valida a quilometragem
        if (quilometragem <= 0) {
            UtilsGUI.avisoErro(this, R.string.quilometragem_invalida);
            editTextQuilometragem.requestFocus();
            return;
        }

        // Valida o valor
        if (valor <= 0) {
            UtilsGUI.avisoErro(this, R.string.valor_invalido);
            editTextValor.requestFocus();
            return;
        }

        manutencao.setDescricao(descricao);
        manutencao.setTipo(spinnerTipo.getSelectedItem().toString());
        manutencao.setQuilometragem(quilometragem);
        manutencao.setValor(valor);

        Veiculo veiculo = (Veiculo) spinnerVeiculo.getSelectedItem();
        if (veiculo == null) {
            UtilsGUI.avisoErro(this, R.string.veiculo_invalido);
            spinnerVeiculo.requestFocus();
            return;
        }

        manutencao.setVeiculo(veiculo);

        try {

            ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);

            if (modo == NOVO) {

                conexao.getManutencaoDao().create(manutencao);

            } else {

                conexao.getManutencaoDao().update(manutencao);
            }

            setResult(Activity.RESULT_OK);
            finish();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cancelar() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private void novoVeiculo() {
        VeiculoActivity.novo(this, REQUEST_NOVO_VEICULO);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicao_detalhes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()){

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemCancelar:
                cancelar();
                return true;
            default:
                // Ação do botão "voltar"
                if (pref_recover("activity").equals("novaManutencao") || pref_recover("activity").equals("inicioManutencao")) {
                    pref_save("activity", "inicioManutencao");
                }

                return super.onOptionsItemSelected(item);
        }
    }

    public void pref_save(String chave, String valor) {
        SharedPreferences.Editor editor = getSharedPreferences(SAVENAME, MODE_PRIVATE).edit();
        editor.putString(chave, valor);
        editor.apply();
    }

    public String pref_recover(String chave) {
        SharedPreferences prefs = getSharedPreferences(SAVENAME, MODE_PRIVATE);
        return prefs.getString(chave, null);
    }

}
