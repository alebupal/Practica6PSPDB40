package com.example.alejandro.practica6aaddb4o;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private ArrayList<Pelicula> datosPeliculas;
    private AdaptadorArrayList ad;
    private ObjectContainer bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cargarDatos();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_anadir) {
            return anadir();
        } else if (id == R.id.action_fecha) {
            ordenarFecha();
        } else if (id == R.id.action_nombre) {
            ordenarNombre();
        } else if (id == R.id.action_genero) {
            ordenarGenero();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.contextual, menu);
    }
    public boolean onContextItemSelected(MenuItem item) {
        int id = item.getItemId();
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int index = info.position;
        if (id == R.id.action_borrar) {
            borrar(index);
        } else if (id == R.id.action_editar) {
            editar(index);
        }
        return super.onContextItemSelected(item);
    }

    private boolean anadir() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.m_anadir));
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.anadir, null);
        alert.setView(vista);
        alert.setPositiveButton(getString(R.string.m_anadir), new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int whichButton) {
                EditText etTitulo, etAnio, etGenero;
                etTitulo = (EditText) vista.findViewById(R.id.etTitulo);
                etAnio = (EditText) vista.findViewById(R.id.etAnio);
                etGenero = (EditText) vista.findViewById(R.id.etGenero);
                if(etTitulo.getText().toString().equals("")==true || etAnio.getText().toString().equals("")==true || etGenero.getText().toString().equals("")==true){
                    tostada(getString(R.string.vacios));
                }else{
                    Pelicula p = new Pelicula();
                    p.setTitulo(etTitulo.getText().toString());
                    p.setAnio(Integer.parseInt(etAnio.getText().toString()));
                    p.setGenero(etGenero.getText().toString());
                    datosPeliculas.add(p);

                    insert(p);
                    ad.notifyDataSetChanged();
                    tostada(getString(R.string.p_anadida));
                }
            }
        });
        alert.setNegativeButton(getString(R.string.cancelar), null);
        alert.show();
        return true;
    }
    private boolean borrar(final int pos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.borrar));
        LayoutInflater inflater = LayoutInflater.from(this);
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Pelicula pborrar = new Pelicula();
                pborrar = datosPeliculas.get(pos);
                datosPeliculas.remove(pos);
                delete(pborrar);
                ad.notifyDataSetChanged();
                tostada(getString(R.string.p_borrada));
            }
        });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
        return true;
    }
    private boolean editar(final int index) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getString(R.string.m_editar));
        LayoutInflater inflater = LayoutInflater.from(this);
        final View vista = inflater.inflate(R.layout.editar, null);
        alert.setView(vista);
        final Pelicula pAntigua = new Pelicula();
        final EditText etTitulo, etAnio, etGenero;
        etTitulo = (EditText) vista.findViewById(R.id.etTitulo2);
        etAnio = (EditText) vista.findViewById(R.id.etAnio2);
        etGenero = (EditText) vista.findViewById(R.id.etGenero2);

        pAntigua.setTitulo(datosPeliculas.get(index).getTitulo().toString());
        pAntigua.setAnio(Integer.parseInt(datosPeliculas.get(index).getAnio().toString()));
        pAntigua.setGenero(datosPeliculas.get(index).getGenero().toString());

        etTitulo.setText(pAntigua.getTitulo());
        etAnio.setText(pAntigua.getAnio().toString());
        etGenero.setText(pAntigua.getGenero());

        alert.setPositiveButton(getString(R.string.m_editar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Pelicula pNueva = new Pelicula();
                if(etTitulo.getText().toString().equals("")==true || etAnio.getText().toString().equals("")==true || etGenero.getText().toString().equals("")==true){
                    tostada(getString(R.string.vacios));
                }else{
                    pNueva.setTitulo(etTitulo.getText().toString());
                    pNueva.setAnio(Integer.parseInt(etAnio.getText().toString()));
                    pNueva.setGenero(etGenero.getText().toString());

                    datosPeliculas.set(index, pNueva);
                    edit(pNueva,pAntigua);
                    ad.notifyDataSetChanged();
                    tostada(getString(R.string.p_modificar));
                }
            }
        });
        alert.setNegativeButton(android.R.string.no, null);
        alert.show();
        return true;
    }
    public void cargarDatos(){
        datosPeliculas = new ArrayList<Pelicula>();

        datosPeliculas.clear();
        list();


        ad = new AdaptadorArrayList(this, R.layout.lista_detalle, datosPeliculas);
        final ListView lv = (ListView) findViewById(R.id.lvLista);
        lv.setAdapter(ad);
        registerForContextMenu(lv);

    }

    public void ordenarFecha(){
        Collections.sort(datosPeliculas, new Comparator<Pelicula>() {
            @Override
            public int compare(Pelicula o1, Pelicula o2) {
                return o1.getAnio().compareTo(o2.getAnio());
            }
        });
        ad.notifyDataSetChanged();
    }
    public void ordenarGenero(){
        Collections.sort(datosPeliculas, new Comparator<Pelicula>() {
            @Override
            public int compare(Pelicula o1, Pelicula o2) {
                return o1.getGenero().compareToIgnoreCase(o2.getGenero());
            }
        });
        ad.notifyDataSetChanged();
    }
    public void ordenarNombre(){
        Collections.sort(datosPeliculas, new Comparator<Pelicula>() {
            @Override
            public int compare(Pelicula o1, Pelicula o2) {
                return o1.getTitulo().compareToIgnoreCase(o2.getTitulo());
            }
        });
        ad.notifyDataSetChanged();
    }

    private void tostada(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }



    public void insert(final Pelicula pNueva){
        bd.store(pNueva);
    }
    public  void edit(final Pelicula pNueva, final Pelicula pAntigua){
        ObjectSet<Pelicula> peliculas = bd.query(new Predicate<Pelicula>() {
            @Override
            public boolean match(Pelicula p) {
                return p.getAnio().compareTo(pAntigua.getAnio())==0 && p.getGenero().compareTo(pAntigua.getGenero())==0 && p.getTitulo().compareTo(pAntigua.getTitulo())==0;
            }
        });
        if (peliculas.hasNext()){
            Pelicula p = peliculas.next();
            p.setGenero(pNueva.getGenero());
            p.setAnio(pNueva.getAnio());
            p.setTitulo(pNueva.getTitulo());
            bd.store(p);
            bd.commit();
        }
    }
    public void delete(final Pelicula pEliminar){
        ObjectSet<Pelicula> peliculas = bd.query( new Predicate<Pelicula>() {
            @Override
            public boolean match(Pelicula p) {
                return p.getAnio().compareTo(pEliminar.getAnio())==0 && p.getGenero().compareTo(pEliminar.getGenero())==0 && p.getTitulo().compareTo(pEliminar.getTitulo())==0;
            }
        });
        if (peliculas.hasNext()){
            Pelicula p = peliculas.next();
            bd.delete(p);
            bd.commit();
        }
    }
    public void list(){
        bd = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), getExternalFilesDir(null) +"/bd.db4o");
        List<Pelicula> peliculas= bd.query(Pelicula.class);
        for(Pelicula p: peliculas){
            datosPeliculas.add(p);
        }
    }



    @Override
    protected void onDestroy() {
        bd.close();
        super.onDestroy();
    }


}
