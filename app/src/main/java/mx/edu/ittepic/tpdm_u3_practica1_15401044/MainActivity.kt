package mx.edu.ittepic.tpdm_u3_practica1_15401044

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    var descripcion:EditText?=null
    var monto:EditText?=null
    var fecha:EditText?=null
    var pagado:CheckBox?=null
    var insertar:Button?=null
    var listView:ListView?=null
    //Declarando el objeto FIRESTORE
    var baseRemota=FirebaseFirestore.getInstance()
    //declarar objetos tipo ARREGLO DINAMICO
    var registrosRemotos=ArrayList<String>()
    var keys=ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        descripcion=findViewById(R.id.txtDescripcion)
        monto=findViewById(R.id.txtMonto)
        fecha=findViewById(R.id.txtFecha)
        insertar=findViewById(R.id.btnInsertar)
        listView=findViewById(R.id.ListView)
        pagado=findViewById(R.id.checkPagado)

        insertar?.setOnClickListener {
            if(validarCampos(descripcion!!,monto!!,fecha!!)==false){
                mensaje("ERROR","AL PARECER HAY UN CAMPO DE TEXTO VACIO")
                return@setOnClickListener }
            var tpagado=""
            if(pagado?.isChecked==true){ tpagado="true" }
            else {tpagado="false"}
            var datosInsertar= hashMapOf(
                "descripcion" to descripcion?.text.toString(),
                "monto" to monto?.text.toString(),
                "fecha_vencimiento" to fecha?.text.toString(),
                "pagado" to tpagado)
            //conexion de bd; add --> insertar, eliminar -->.document(ID).(set ,delete,get) set=actualizar,delete=eliminar,get=obtener
            baseRemota.collection("recibos")
                .add(datosInsertar as Map<String,Any>) //as Map<String,Any>
                .addOnSuccessListener {//Respuesta satisfactoria de que si se inserto internet
                    Toast.makeText(this,"Inserción correcta", Toast.LENGTH_LONG)
                        .show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"Error en inserción", Toast.LENGTH_LONG)
                        .show()

                }  //Cuando falla una insercion  internet
            limpiarCampos()
        }//insertar
        //mostrar en lista
        baseRemota.collection("recibos")
            .addSnapshotListener { querySnapshot, e ->
                if(e!=null){
                    Toast.makeText(this,"Error no se puede consultar",Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }
                registrosRemotos.clear()
                keys.clear()
                for(document in querySnapshot!!){
                    var cadena= document.getString("descripcion")+"\n"+"$"+
                            document.getString("monto")+", fecha : "+
                            document.getString("fecha_vencimiento")
                    registrosRemotos.add(cadena)
                    keys.add(document.id)
                }
                if(registrosRemotos.size==0){registrosRemotos.add("NO HAY DATOS AUN PARA MOSTRAR")}
                var adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,registrosRemotos)
                listView?.adapter=adapter
            }

        listView?.setOnItemClickListener { parent, view, position, id ->
            if(keys.size==0){return@setOnItemClickListener}

            AlertDialog.Builder(this).setTitle("ATENION")
                .setMessage(" ¿Qué deseas hacer con :\n "+registrosRemotos.get(position)+" ?")
                .setPositiveButton("Eliminar"){dialog,which->
                    baseRemota.collection("recibos")
                        .document(keys.get(position)).delete()  //Eliminar Registro
                        .addOnSuccessListener {
                            Toast.makeText(this,"Recibo de pago eliminado",Toast.LENGTH_SHORT)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,"Recibo de pago no eliminado",Toast.LENGTH_SHORT)
                        }
                }
                .setNegativeButton("Actualizar"){dialog,which->
                    var ventanaActualizar= Intent(this,Main2Activity::class.java)
                    ventanaActualizar.putExtra("id",keys.get(position))
                    startActivity(ventanaActualizar)
                }
                .setNeutralButton("Cancelar"){dialog,which->}
                .show()

        }

    }//onCreate
    fun limpiarCampos(){
        descripcion?.setText("")
        monto?.setText("")
        fecha?.setText("")
        pagado?.setChecked(false)
    }
    fun mensaje(t:String,m:String) {
        AlertDialog.Builder(this).setTitle(t).setMessage(m)
            .setPositiveButton("OK"){dialog,which->}.show()
    }//Funcion mensaje

    fun validarCampos(descripcion:EditText,monto:EditText,fecha:EditText):Boolean {
        if(descripcion.text.isEmpty() || monto.text.isEmpty() || fecha.text.isEmpty()){return false}
        else
            return true
    }
}//class
