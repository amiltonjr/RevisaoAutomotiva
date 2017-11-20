package com.utfpr.amiltonjr.revisaoautomotiva.persistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.utfpr.amiltonjr.revisaoautomotiva.modelo.Manutencao;
import com.utfpr.amiltonjr.revisaoautomotiva.modelo.Veiculo;

import java.sql.SQLException;

public class ConexaoDatabase extends OrmLiteSqliteOpenHelper {

    private static final String DB_NAME     = "manutencoes.db";
    private static final int    DB_VERSION  = 3;

    private static ConexaoDatabase instance;
    private Dao<Veiculo, Integer>   veiculoDao;
    private Dao<Manutencao, Integer> manutencaoDao;

    public static ConexaoDatabase getInstance(Context contexto){

        if (instance == null){
            instance = new ConexaoDatabase(contexto);
        }

        return instance;
    }
    
    private ConexaoDatabase(Context contexto) {
        super(contexto, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {

            TableUtils.createTable(connectionSource, Veiculo.class);
            TableUtils.createTable(connectionSource, Manutencao.class);

        } catch (SQLException e) {
            Log.e(ConexaoDatabase.class.getName(), "onCreate", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {

            TableUtils.dropTable(connectionSource, Manutencao.class, true);
            TableUtils.dropTable(connectionSource, Veiculo.class, true);

            onCreate(database, connectionSource);

        } catch (SQLException e) {
            Log.e(ConexaoDatabase.class.getName(), "onUpgrade", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Manutencao, Integer> getManutencaoDao() throws SQLException {

        if (manutencaoDao == null) {
            manutencaoDao = getDao(Manutencao.class);
        }
        
        return manutencaoDao;
    }

    public Dao<Veiculo, Integer> getVeiculoDao() throws SQLException {

        if (veiculoDao == null) {
            veiculoDao = getDao(Veiculo.class);
        }

        return veiculoDao;
    }
}