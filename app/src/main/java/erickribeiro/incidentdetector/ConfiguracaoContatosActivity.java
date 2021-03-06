package erickribeiro.incidentdetector;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NotificationCompat;
import android.widget.RadioButton;
import android.widget.Toast;


import java.util.List;

import erickribeiro.incidentdetector.configuracao.Contato;
import erickribeiro.incidentdetector.configuracao.ContatosUtil;
import erickribeiro.incidentdetector.costumizado.MeuContatoPreference;

import static android.content.SharedPreferences.Editor;
import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class ConfiguracaoContatosActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    static final int INTENT_CONTACT  = 1;
    private ContatosUtil mContatos;
    private SharedPreferences prefs_do_contato;
    public static int NUMTYPEPREF;

    NotificationCompat.Builder mBuilder;
    NotificationManager mNotifyManager;
    String TAG = "MainActivity";
    RadioButton start, stop;
    int ID = 101;

    int contSms = 0;


    private PendingIntent pendingIntent;
    @SuppressWarnings("unused")
    private AlarmManager alarmManager;
    private final int TIMER_MONITORAMENTO_HEURISTICA = 30; // EM milisegundos...

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setupSimplePreferencesScreen();

        mContatos = new ContatosUtil(getContentResolver());

        atualizaMeuContatoPreference(1).setTipo(false);
        atualizaMeuContatoPreference(2).setTipo(true);
        atualizaMeuContatoPreference(3).setTipo(true);
        atualizaMeuContatoPreference(4).setTipo(true);
    }

    /**
     * Cria/Atualiza o componente MeuContatoPreference
     * @param numPref
     * @return MeuContatoPreference
     */

    public MeuContatoPreference atualizaMeuContatoPreference(int numPref){
        String desNome;
        String desSummary;

        switch (numPref) {
            case 1:
                desNome = "Adicionar contato";
                desSummary = "Contato que receberá a ligação";
                break;
            default:
                desNome = "Adicionar contato";
                desSummary = "Contato que receberá o sms";
                break;
        }

        SharedPreferences pref = getSharedPreferences("prefs_do_contato", MODE_PRIVATE);

        @SuppressWarnings("deprecation")
        MeuContatoPreference meuContatoPreference = (MeuContatoPreference) getPreferenceScreen().findPreference("pref_key_contato"+numPref);
        meuContatoPreference.setTitle(pref.getString("nomeKey"+numPref, desNome));
        meuContatoPreference.setSummary(pref.getString("telefoneKey"+numPref, desSummary));

        return meuContatoPreference;
    }

    /**
     * Guarda em um SharedPreference de nome prefs_do_contato os dados passados por parametro.
     * @param nome
     * @param telefone
     * @param num
     */
    public void criarPreferenciasContato(String nome, String telefone, int num){
        prefs_do_contato = getSharedPreferences("prefs_do_contato", Context.MODE_PRIVATE);
        Editor editor = prefs_do_contato.edit();
        editor.putString("nomeKey"+num, nome);
        editor.putString("telefoneKey" + num, telefone);
        editor.commit();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        atualizaMeuContatoPreference(1);
        atualizaMeuContatoPreference(2);
        atualizaMeuContatoPreference(3);
        atualizaMeuContatoPreference(4);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @SuppressWarnings("deprecation")
    private void setupSimplePreferencesScreen() {
        addPreferencesFromResource(R.layout.pref_contatos);
        /*
        addPreferencesFromResource(R.xml.pref_general);

        PreferenceCategory fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle("Notificação");
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_notification);

        fakeHeader = new PreferenceCategory(this);
        fakeHeader.setTitle("Contatos");
        getPreferenceScreen().addPreference(fakeHeader);
        addPreferencesFromResource(R.xml.pref_contatos);
        */
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,String key) {

        if(key.equals("pref_key_contato1")){
            Intent it = mContatos.getContatoFromIntent();
            startActivityForResult(it, INTENT_CONTACT);
            ConfiguracaoContatosActivity.NUMTYPEPREF = 1;
            atualizaMeuContatoPreference(NUMTYPEPREF);
        }
        else if(key.equals("pref_key_contato2")){
            Intent it = mContatos.getContatoFromIntent();
            startActivityForResult(it, INTENT_CONTACT);
            ConfiguracaoContatosActivity.NUMTYPEPREF = 2;
            atualizaMeuContatoPreference(NUMTYPEPREF);
        }
        else if(key.equals("pref_key_contato3")){
            Intent it = mContatos.getContatoFromIntent();
            startActivityForResult(it, INTENT_CONTACT);
            ConfiguracaoContatosActivity.NUMTYPEPREF = 3;
            atualizaMeuContatoPreference(NUMTYPEPREF);
        }
        else if(key.equals("pref_key_contato4")) {
            Intent it = mContatos.getContatoFromIntent();
            startActivityForResult(it, INTENT_CONTACT);
            ConfiguracaoContatosActivity.NUMTYPEPREF = 4;
            atualizaMeuContatoPreference(NUMTYPEPREF);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (INTENT_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(
                            contactData, null, null,
                            null, null);
                    List<Contato> contatos = mContatos.getContatosFromCursor(c);
                    if (contatos.size() > 0) {

                        Contato contato = contatos.get(0);
                        if (contato.getTelefones().size() > 0) {
                            switch (ConfiguracaoContatosActivity.NUMTYPEPREF) {
                                case 1:
                                    criarPreferenciasContato(contato.getNome(), contato.getTelefones().get(0).numero, NUMTYPEPREF);
                                    break;
                                case 2:
                                    criarPreferenciasContato(contato.getNome(), contato.getTelefones().get(0).numero, NUMTYPEPREF);
                                    break;

                                case 3:
                                    criarPreferenciasContato(contato.getNome(), contato.getTelefones().get(0).numero, NUMTYPEPREF);
                                    break;

                                case 4:
                                    criarPreferenciasContato(contato.getNome(), contato.getTelefones().get(0).numero, NUMTYPEPREF);
                                    break;

                                default:
                                    break;
                            }

                            Toast.makeText(this, "Nome: "+contato.getNome()+" - Telefone: "+contato.getTelefones().get(0).numero, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
        }
    }
}
