package com.georgesapps.tirapizzas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.gridlayout.widget.GridLayout;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,View.OnLongClickListener, AdapterView.OnItemSelectedListener{
    RelativeLayout tablaLayout;
    Dialog dialogoDificultad;
    Dialog dialogoImageP;
    Dialog dialogoImageG;
    int casillas=8;
    Button boton;
    String[] bombas;
    String[][]arrayIds;
    Button [][]buttons;
    int tamanio;
    int contadorPizza=0;
    boolean click=true;
    boolean clickLargo=true;
    private Object Image;
    private Menu menu;
    int pizzaActual=R.drawable.pizzapepperoni;
    boolean finJuego = false;
    String[] arrayNombresPizza = { "Seleccione Pizza:", "Pizza Pepperoni","Pizza Jamón", "Pizza Campesina","Pizza Carbonara", "Pizza Cuatro Quesos"};
    Integer[] arrayImagenesPizza = { 0, R.drawable.pizzapepperoni, R.drawable.pizzajamon, R.drawable.pizzacampesina,
            R.drawable.pizzacarbonara, R.drawable.pizzacuatroquesos};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tablaLayout = findViewById(R.id.tablaJuego);
        crearTabla(8);

        //declaración de spinner
        Spinner mySpinner = findViewById(R.id.spinner1);

        //Inserción del adaptador al spinner y configuración del adaptador
        mySpinner.setAdapter(new AdaptadorSpinner(MainActivity.this, R.layout.lineas_spinner,arrayNombresPizza));
        //implenmentacion del listener al spinner
        mySpinner.setOnItemSelectedListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflater para acceder al recurso creado en la carpeta menu.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.instrucciones:
                //llamada al método para mostarr las intrucciones
                mostrarInstrucciones();
            case R.id.comenzar:
                //comienza el juego creando una nueva tabla
                crearTabla(casillas);
                return true;
            case R.id.configurar:
                //llamada al método que muestra las opciones de dificultad
                mostrarDificultad();
                return true;
            case R.id.iconopizza:
                //llamada la metodo que muestra el spinner
                mostrarSpinner();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void mostrarInstrucciones() {
        Dialog dialogoInstrucciones = new Dialog(this);
        dialogoInstrucciones.setContentView(R.layout.instrucciones);
        dialogoInstrucciones.show();
    }

    private void mostrarDificultad() {
        dialogoDificultad = new Dialog(this);
        dialogoDificultad.setContentView(R.layout.dificultad);
        dialogoDificultad.show();
    }

    @SuppressLint("NewApi")
    public void marcarDificultad(View v){
        String dificultad = v.getTag().toString();
        casillas=Integer.parseInt(dificultad);
        bombas=arrayPizzas(casillas);
        crearTabla(Integer.parseInt(dificultad));
    }


    public void crearTabla(int casillas) {
        finJuego=false;
        bombas=arrayPizzas(casillas);
        buttons = new Button[casillas][casillas];
        arrayIds=new String[casillas][casillas];
        for (int i = 0; i < casillas; i++) {
            for (int j = 0; j < casillas; j++) {
                arrayIds[i][j]= i +"-"+ j;
            }
        }
        GridLayout tabla = findViewById(R.id.gridTabla);
        tabla.removeAllViews();
        tabla.setRowCount(casillas);
        tabla.setColumnCount(casillas);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        //CON LOS SIGUEINTES MÉTODOS RECOJO EL TAMAÑO DEL ACTION BAR Y DEL MENÚ PARA DESPUÉS RESTARSELO A LA PANTALLA Y CALCULAR EL ESPACIO DE LAS CELDAS
        int notificationBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            notificationBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        int actionBarHeight=0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
        }

        int width = size.x;
        int height = size.y - actionBarHeight - notificationBarHeight;
        tabla.setOrientation(GridLayout.HORIZONTAL);
        for (int i = 0; i < casillas; i++) {
            for (int j = 0; j < casillas; j++) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((width-casillas)/casillas, (height-casillas)/casillas);
                //params.setMargins(0,0,0,0);
                boton = new Button(this);
                boton.setLayoutParams(params);
                boton.setTag(i +"-"+ j);
                boton.setOnClickListener(this);
                boton.setOnLongClickListener(this);
                boton.setTextColor(View.INVISIBLE);
                GradientDrawable border = new GradientDrawable();
                border.setColor(Color.rgb(71, 107, 107));
                border.setStroke(2,Color.BLACK);
                boton.setBackground(border);
                String tag = String.valueOf( boton.getTag());
                for(String c:bombas){
                    if(c.equals(tag)){
                        boton.setTag("9");
                        boton.setText("9");
                        if(casillas==16){
                            boton.setTextSize(10);
                        }
                        break;
                    }
                }
                arrayIds[i][j]= (String) boton.getTag();
                buttons[i][j]=boton;
                tabla.addView(boton);
            }
        } colocarNumeros();
    }
    public void colocarNumeros(){
        for(int i=0;i<casillas;i++){
            for(int j=0;j<casillas;j++){
                int contador = 0;
                if(!String.valueOf(buttons[i][j].getTag()).equals("9")) {
                    if (i > 0) {
                        if (String.valueOf(buttons[i - 1][j].getTag()).equals("9")) {
                            contador++;
                        }
                    }
                    if (i > 0 && j > 0) {
                        if (String.valueOf(buttons[i - 1][j-1].getTag()).equals("9")) {
                            contador++;
                        }
                    }
                    if (i > 0 && j < casillas-1)
                        if (String.valueOf(buttons[i - 1][j+1].getTag()).equals("9"))
                            contador++;

                    if (j>0)
                        if (String.valueOf(buttons[i][j-1].getTag()).equals("9"))
                            contador++;

                    if (j<casillas-1)
                        if (String.valueOf(buttons[i][j+1].getTag()).equals("9"))
                            contador++;

                    if(i<casillas-1){
                        if (String.valueOf(buttons[i+1][j].getTag()).equals("9")) {
                            contador++;
                        }
                    }
                    if(i<casillas-1&&j>0){
                        if (String.valueOf(buttons[i+1][j-1].getTag()).equals("9")) {
                            contador++;
                        }
                    }
                    if(i<casillas-1&&j<casillas-1){
                        if (String.valueOf(buttons[i+1][j+1].getTag()).equals("9")) {
                            contador++;
                        }
                    }
                    buttons[i][j].setText(String.valueOf(contador));
                    if(casillas==16){
                        buttons[i][j].setTextSize(10);
                    }
                }
            }

        }
    }


    public void ocultarDialog(View v){
        Button b = (Button) v;
        String tagB = (String) b.getTag();
        if(tagB.equals("bd")){
            dialogoDificultad.dismiss();
        }else if(tagB.equals("bp")) {
            dialogoImageP.dismiss();
        }else if(tagB.equals("bg")) {
            dialogoImageG.dismiss();
        }
    }

    public String[] arrayPizzas (int casillas){
        tamanio=0;
        if(casillas==8){
            tamanio=10;
        }else if(casillas==12){
            tamanio=30;
        }else if(casillas==16){
            tamanio=60;
        }
        String []casillasPizza = new String[tamanio];

            for (int x=0; x < tamanio; x++){
                int rnd = (int) (Math.random()*casillas);
                int rnd2 = (int) (Math.random()*casillas);
                casillasPizza[x]=rnd+"-"+rnd2;
                for(int y=0; y < x; y++){
                    if (casillasPizza[x].equals(casillasPizza[y])){
                        x--;
                        break;
                    }
                }

            }
        return  casillasPizza;
    }
    public void onClick(View v) {
        if(!finJuego) {
            if (clickLargo) {
                Button b = (Button) v;

                if (b.getTag().equals("9")) {
                    b.setBackgroundResource(pizzaActual);
                    mostrarImageP();
                    contadorPizza = 0;
                    finJuego=true;
                } else if (b.getText().equals("0")) {
                    ((Button) v).setTextColor(Color.RED);
                    explosion(b);
                } else {
                    b.setBackgroundColor(Color.rgb(51, 77, 77));
                    ((Button) v).setTextColor(Color.RED);
                    b.setEnabled(false);
                }
            }
            clickLargo = true;
        }
    }
    public boolean onLongClick(View v) {
        if(!finJuego) {
            clickLargo=false;
            Button b = (Button) v;

            if (!b.getTag().equals("9")) {
                b.setTextColor(View.VISIBLE);
                b.setBackgroundResource(R.drawable.x);
                ((Button) v).setTextColor(Color.RED);
                mostrarImageP();
                contadorPizza=0;
                finJuego=true;
            } else if (b.getTag().equals("9")) {
                b.setBackgroundResource(pizzaActual);
                contadorPizza++;
                clickLargo=true;
                b.setEnabled(false);
                if (contadorPizza == tamanio) {
                    mostrarImageG();
                    finJuego=true;
                }
            }
        }
        return false;

    }
    public void explosion(Button b){
        String posicion=String.valueOf(b.getTag());
        if(!posicion.equals("9")) {
            int i = Integer.valueOf(posicion.split("-")[0]);
            int j = Integer.valueOf(posicion.split("-")[1]);
            buttons[i][j].setText(" ");
            b.setBackgroundColor(Color.RED);
            b.setEnabled(false);
            if (i > 0) {
                if (buttons[i - 1][j].getText().equals("0")) {
                    explosion(buttons[i - 1][j]);
                }else if(!buttons[i - 1][j].getText().equals("0")&&!buttons[i - 1][j].getText().equals(" ")){
                    buttons[i - 1][j].setBackgroundColor(Color.rgb(51, 77, 77));
                    buttons[i - 1][j].setTextColor(Color.RED);
                    buttons[i - 1][j].setEnabled(false);
                }
            }
            if (j > 0) {
                if (buttons[i][j - 1].getText().equals("0")) {
                    explosion(buttons[i][j - 1]);
                }else if(!buttons[i][j - 1].getText().equals("0")&&!buttons[i][j-1].getText().equals(" ")){
                    buttons[i][j - 1].setBackgroundColor(Color.rgb(51, 77, 77));
                    buttons[i][j - 1].setTextColor(Color.RED);
                    buttons[i][j - 1].setEnabled(false);
                }
            }
            if (j < casillas - 1) {
                if (buttons[i][j + 1].getText().equals("0")) {
                    explosion(buttons[i][j + 1]);
                }else if(!buttons[i][j + 1].getText().equals("0")&&!buttons[i][j+1].getText().equals(" ")){
                    buttons[i][j + 1].setBackgroundColor(Color.rgb(51, 77, 77));
                    buttons[i][j + 1].setTextColor(Color.RED);
                    buttons[i][j + 1].setEnabled(false);
                }
            }
            if (i < casillas - 1) {
                if (buttons[i + 1][j].getText().equals("0")) {
                    explosion(buttons[i + 1][j]);
                }else if(!buttons[i + 1][j].getText().equals("0")&&!buttons[i + 1][j].getText().equals(" ")){
                    buttons[i + 1][j].setBackgroundColor(Color.rgb(51, 77, 77));
                    buttons[i + 1][j].setTextColor(Color.RED);
                    buttons[i + 1][j].setEnabled(false);
                }
            }
            if(i>0 && j>0){
                if(!buttons[i - 1][j - 1].getText().equals("0")&&!buttons[i - 1][j - 1].getText().equals(" ")){
                    buttons[i - 1][j - 1].setBackgroundColor(Color.rgb(51, 77, 77));
                    buttons[i - 1][j - 1].setTextColor(Color.RED);
                    buttons[i - 1][j - 1].setEnabled(false);
                }
            }
            if(i<casillas-1 && j<casillas-1){
                if(!buttons[i + 1][j + 1].getText().equals("0")&&!buttons[i + 1][j + 1].getText().equals(" ")){
                    buttons[i + 1][j + 1].setBackgroundColor(Color.rgb(51, 77, 77));
                    buttons[i + 1][j + 1].setTextColor(Color.RED);
                    buttons[i + 1][j + 1].setEnabled(false);
                }
            }
            if(i<casillas-1 && j>0){
                if(!buttons[i + 1][j - 1].getText().equals("0")&&!buttons[i + 1][j - 1].getText().equals(" ")){
                    buttons[i + 1][j - 1].setBackgroundColor(Color.rgb(51, 77, 77));
                    buttons[i + 1][j - 1].setTextColor(Color.RED);
                    buttons[i + 1][j - 1].setEnabled(false);
                }
            }
            if(i>0 && j<casillas-1){
                if(!buttons[i - 1][j + 1].getText().equals("0")&&!buttons[i - 1][j + 1].getText().equals(" ")){
                    buttons[i - 1][j + 1].setBackgroundColor(Color.rgb(51, 77, 77));
                    buttons[i - 1][j + 1].setTextColor(Color.RED);
                    buttons[i - 1][j + 1].setEnabled(false);
                }
            }
        }
    }
    public void mostrarImageP(){
        dialogoImageP = new Dialog(this);
        dialogoImageP.setContentView(R.layout.imagen_perdido);
        dialogoImageP.show();
    }
    public void mostrarImageG(){
        dialogoImageG = new Dialog(this);
        dialogoImageG.setContentView(R.layout.imagen_ganado);
        dialogoImageG.show();
    }

    public void mostrarSpinner(){
        if(findViewById(R.id.spinner1).getVisibility()==View.VISIBLE) {
            findViewById(R.id.spinner1).setVisibility(View.INVISIBLE);
        }else
            findViewById(R.id.spinner1).setVisibility(View.VISIBLE);
    }
    //método que recoge el click de la selección del spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(findViewById(R.id.spinner1).getVisibility()==View.VISIBLE) {
            findViewById(R.id.spinner1).setVisibility(View.INVISIBLE);
        }else
            findViewById(R.id.spinner1).setVisibility(View.VISIBLE);
        //
        switch(position){
            case 1:
                pizzaActual=R.drawable.pizzapepperoni;
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.pizzapepperoni));
                crearTabla(casillas);
                break;
            case 2:
                pizzaActual=R.drawable.pizzajamon;
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.pizzajamon));
                crearTabla(casillas);
                break;
            case 3:
                pizzaActual=R.drawable.pizzacarbonara;
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.pizzacarbonara));
                crearTabla(casillas);
                break;
            case 4:
                pizzaActual=R.drawable.pizzacampesina;
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.pizzacampesina));
                crearTabla(casillas);
                break;
            case 5:
                pizzaActual=R.drawable.pizzacuatroquesos;
                menu.getItem(0).setIcon(ContextCompat.getDrawable(this, R.drawable.pizzacuatroquesos));
                crearTabla(casillas);
                break;
            default:
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public class AdaptadorSpinner extends ArrayAdapter {

        public AdaptadorSpinner(Context context, int textViewResourceId,
                         String[] objects) {
            super(context, textViewResourceId, objects);
        }

        public View getLineasSpinner(int position, View convertView, ViewGroup parent) {
            // Inflando el layout del Spinner
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.lineas_spinner, parent, false);

            // Declaración del textView de lineas_spinner
            TextView nombrePizza = layout.findViewById(R.id.nombrePizza);
            // Introducir el texto del array en la vista
            nombrePizza.setText(arrayNombresPizza[position]);
            nombrePizza.setTextColor(Color.rgb(75, 180, 225));

            // Declaracion de la imageView de lineas_spinner
            ImageView imgPizza = layout.findViewById(R.id.imgPizza);

            // Introducir la imagen del array en la vista
            imgPizza.setImageResource(arrayImagenesPizza[position]);

            // Setting Special atrributes for 1st element
            if (position == 0) {
            // Removing the image view
                imgPizza.setVisibility(View.GONE);
            // Setting the size of the text
                nombrePizza.setTextSize(20f);
            // Setting the text Color
                nombrePizza.setTextColor(Color.BLACK);

            }

            return layout;
        }

        //Método que muestra una vista con un dato específico en el  drop down popup
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getLineasSpinner(position, convertView, parent);
        }

        //Método que muestra una vista con un dato específico
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getLineasSpinner(position, convertView, parent);
        }
    }
}

