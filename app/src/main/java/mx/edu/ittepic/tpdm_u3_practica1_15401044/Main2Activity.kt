package mx.edu.ittepic.tpdm_u3_practica1_15401044

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore

class Main2Activity : AppCompatActivity() {
    var btnRegresar:Button?=null
    var btnActualizar:Button?=null
    var descripcion: EditText?=null
    var monto: EditText?=null
    var fecha: EditText?=null
    var pagado: CheckBox?=null
    var id=""
    //Declarando el objeto FIRESTORE
    var baseRemota= FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        descripcion=findViewById(R.id.txtDescripcion)
        monto=findViewById(R.id.txtMonto)
        fecha=findViewById(R.id.txtFecha)
        pagado=findViewById(R.id.checkPagado)
        btnRegresar=findViewById(R.id.btnRegresar)
        btnActualizar=findViewById(R.id.btnActualizar)

        btnRegresar?.setOnClickListener { finish() }

        id= intent.extras?.getString("id").toString()

        baseRemota.collection("recibos")
            .document(id)
            .get()
            .addOnSuccessListener {
                descripcion?.setText(it.getString("descripcion"))
                monto?.setText(it.getString("monto"))
                fecha?.setText(it.getString("fecha_vencimiento"))
                var tpago=it.getString("pagado")
                if(tpago=="true") {
                    pagado?.setChecked(true) }
                else  pagado?.setChecked(false)
            }
            .addOnFailureListener {
                descripcion?.setText("NULL")
                monto?.setText("NULL")
                fecha?.setText("NULL")
                pagado?.setChecked(false)

                descripcion?.isEnabled=false
                monto?.isEnabled=false
                fecha?.isEnabled=false
                pagado?.setChecked(false)
                btnActualizar?.isEnabled=false
            }

        btnActualizar?.setOnClickListener {
            var datosActualizar= hashMapOf(
                "descripcion" to descripcion?.text.toString(),
                "monto" to monto?.text.toString(),
                "fecha_vencimiento" to fecha?.text.toString(),
                "pagado" to pagado?.isChecked()
            )

            baseRemota.collection("recibos")
                .document(id)
                .set(datosActualizar as Map <String,Any>)
                .addOnSuccessListener {
                    limpiarCampos()
                    Toast.makeText(this,"SE ACTUALIZO RECIBO", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this,"NO SE PUDO ACTUALIZAR RECIBO", Toast.LENGTH_LONG).show()
                }
        }
    }//onCreate

    fun limpiarCampos(){
        descripcion?.setText("")
        monto?.setText("")
        fecha?.setText("")
        pagado?.setChecked(false)
    }
}
