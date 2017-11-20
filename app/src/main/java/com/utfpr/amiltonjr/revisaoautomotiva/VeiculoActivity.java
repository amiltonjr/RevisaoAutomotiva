package com.utfpr.amiltonjr.revisaoautomotiva;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.utfpr.amiltonjr.revisaoautomotiva.modelo.Veiculo;
import com.utfpr.amiltonjr.revisaoautomotiva.persistencia.ConexaoDatabase;
import com.utfpr.amiltonjr.revisaoautomotiva.utils.UtilsGUI;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class VeiculoActivity extends AppCompatActivity {

    public static final String MODO    = "MODO";
    public static final String ID      = "ID";
    public static final int    NOVO    = 1;
    public static final int    ALTERAR = 2;

    private EditText    editTextPlaca;
    private EditText    editTextMarca;
    private EditText    editTextModelo;
    private EditText    editTextCor;
    private Spinner     spinnerCategoria;

    private int modo;
    private int posicao = -1;
    private Veiculo veiculo;
    private boolean editado = false;
    private String nome_original;

    public static void novo(Activity activity, int requestCode) {

        Intent intent = new Intent(activity, VeiculoActivity.class);

        intent.putExtra(MODO, NOVO);

        activity.startActivityForResult(intent, requestCode);
    }

    public static void alterar(Activity activity, int requestCode, Veiculo veiculo){

        Intent intent = new Intent(activity, VeiculoActivity.class);

        intent.putExtra(MODO, ALTERAR);
        intent.putExtra(ID, veiculo.getId());

        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veiculo);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        editTextPlaca       = (EditText) findViewById(R.id.editTextDescricao);
        editTextMarca       = (EditText) findViewById(R.id.editTextMarca);
        editTextModelo      = (EditText) findViewById(R.id.editTextModelo);
        editTextCor         = (EditText) findViewById(R.id.editTextCor);
        spinnerCategoria    = (Spinner) findViewById(R.id.spinnerCategoriaVeiculo);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        modo = bundle.getInt(MODO);

        if (modo == ALTERAR) {

            int id = bundle.getInt(ID);

            try {

                ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);
                veiculo =  conexao.getVeiculoDao().queryForId(id);

                editTextPlaca.setText(veiculo.getPlaca());
                editTextMarca.setText(veiculo.getMarca());
                editTextModelo.setText(veiculo.getModelo());
                editTextCor.setText(veiculo.getCor());

                posicao = posicaoCategoria(veiculo.getCategoria());
                spinnerCategoria.setSelection(posicao);

            } catch (SQLException e) {
                e.printStackTrace();
            }

            setTitle(R.string.alterar_veiculo);

        } else {

            veiculo = new Veiculo();

            setTitle(R.string.novo_veiculo);
        }

        // Listener da placa
        editTextPlaca.addTextChangedListener(new TextWatcher() {

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

        // Listener da marca
        editTextMarca.addTextChangedListener(new TextWatcher() {

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

        // Listener do modelo
        editTextModelo.addTextChangedListener(new TextWatcher() {

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

        // Listener da cor
        editTextCor.addTextChangedListener(new TextWatcher() {

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

        // Listener da categoria
        spinnerCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

    private void salvar() {

        String placa  = UtilsGUI.validaCampoTexto(this, editTextPlaca, R.string.placa_vazia);
        String marca = UtilsGUI.validaCampoTexto(this, editTextMarca, R.string.marca_vazia);
        String modelo = UtilsGUI.validaCampoTexto(this, editTextModelo, R.string.modelo_vazio);
        String cor = UtilsGUI.validaCampoTexto(this, editTextCor, R.string.cor_vazia);
        Spinner s_categoria = (Spinner) findViewById(R.id.spinnerCategoriaVeiculo);
        String categoria = s_categoria.getSelectedItem().toString();

        if (placa == null) {
            return;
        }

        try {

            ConexaoDatabase conexao = ConexaoDatabase.getInstance(this);

            if (modo == NOVO) {
                List<Veiculo> lista = conexao.getVeiculoDao().queryBuilder().where().eq("placa", placa).query();

                if (lista != null && lista.size() > 0) {
                    UtilsGUI.avisoErro(this, R.string.placa_usada);
                    return;
                }
            }

            veiculo.setPlaca(placa);
            veiculo.setMarca(marca);
            veiculo.setModelo(modelo);
            veiculo.setCor(cor);
            veiculo.setCategoria(categoria);

            if (modo == NOVO) {

                conexao.getVeiculoDao().create(veiculo);

            } else {

                conexao.getVeiculoDao().update(veiculo);
            }

            setResult(Activity.RESULT_OK);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancelar() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    private int posicaoCategoria(String categoria) {

        List<String> listaCategorias = Arrays.asList(getResources().getStringArray(R.array.veiculo_categoria));

        try {
            for (int pos = 0; pos < listaCategorias.size(); pos++) {

                String t = listaCategorias.get(pos);

                if (t.equals(categoria))
                    return pos;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();

            return -1;
        }

        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edicao_detalhes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {

            case R.id.menuItemSalvar:
                salvar();
                return true;
            case R.id.menuItemCancelar:
                cancelar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
