package com.example.dispositivosmoviles

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.findNavController
import java.io.IOException
import java.util.*

const val REQUEST_ENABLE_BT = 1
private const val PARSED_PRODUCTS = "parsedProducts"

class BluetoothFragment : Fragment() {
    lateinit var mBtAdapter: BluetoothAdapter
    var mAddressDevices: ArrayAdapter<String>? = null
    var mNameDevices: ArrayAdapter<String>? = null
    private lateinit var root: ViewGroup
    private var parsedProducts: String? = null

    companion object {
        var m_myUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9834FB")
        private var m_bluetoothSocket: BluetoothSocket? = null

        var m_isConnected: Boolean = false
        lateinit var m_address: String
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            parsedProducts = it.getString(PARSED_PRODUCTS).toString()
        }

        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (!isGranted) {
                    goToWeightProducts()
                    Toast.makeText(requireActivity(), "Error: No se tiene permiso de Bluetooth", Toast.LENGTH_SHORT).show()
                }
            }

        requestPermissionLauncher.launch(android.Manifest.permission.BLUETOOTH_CONNECT)
    }

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_weight_products, container, false) as ViewGroup

        mAddressDevices = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1)
        mNameDevices = ArrayAdapter(requireActivity(), android.R.layout.simple_list_item_1)

        val buttonEncenderBluetooth = root.findViewById<Button>(R.id.idBtnOnBT)
        val buttonApagarBluetooth = root.findViewById<Button>(R.id.idBtnOffBT)
        val buttonConectar = root.findViewById<Button>(R.id.idBtnConnect)

        val buttonBuscarDispositivos = root.findViewById<Button>(R.id.idBtnDispBT)
        val spinnerDispositivos = root.findViewById<Spinner>(R.id.idSpinDisp)

        val someActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {}

        mBtAdapter = (requireActivity().getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter

        if (mBtAdapter == null) {
            Toast.makeText(requireActivity(), "Bluetooth no esta disponible en este dispositivo", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireActivity(), "Bluetooth disponible en este dispositivo", Toast.LENGTH_LONG).show()
        }

        buttonEncenderBluetooth.setOnClickListener {
            if (mBtAdapter.isEnabled) {
                Toast.makeText(requireActivity(), "Bluetooth ya se encuentra activado", Toast.LENGTH_LONG).show()
            } else {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                someActivityResultLauncher.launch(enableBtIntent)
            }
        }

        buttonApagarBluetooth.setOnClickListener {
            if (!mBtAdapter.isEnabled) {
                Toast.makeText(requireActivity(), "Bluetooth ya se encuentra desactivado", Toast.LENGTH_LONG).show()
            } else {
                mBtAdapter.disable()
                Toast.makeText(requireActivity(), "Se ha desactivado el bluetooth", Toast.LENGTH_LONG).show()
            }
        }

        buttonBuscarDispositivos.setOnClickListener {
            if (mBtAdapter.isEnabled) {

                val pairedDevices: Set<BluetoothDevice>? = mBtAdapter.bondedDevices
                mAddressDevices!!.clear()
                mNameDevices!!.clear()

                pairedDevices?.forEach { device ->
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address
                    mAddressDevices!!.add(deviceHardwareAddress)
                    mNameDevices!!.add(deviceName)
                }

                spinnerDispositivos.adapter = mNameDevices
            } else {
                val noDevices = "Ningun dispositivo pudo ser emparejado"
                mAddressDevices!!.add(noDevices)
                mNameDevices!!.add(noDevices)
                Toast.makeText(requireActivity(), "Primero vincule un dispositivo bluetooth", Toast.LENGTH_LONG).show()
            }
        }

        buttonConectar.setOnClickListener {
            try {
                if (m_bluetoothSocket == null || !m_isConnected) {

                    val intValSpin = spinnerDispositivos.selectedItemPosition
                    m_address = mAddressDevices!!.getItem(intValSpin).toString()
                    Toast.makeText(requireActivity(), m_address, Toast.LENGTH_LONG).show()
                    mBtAdapter.cancelDiscovery()
                    val device: BluetoothDevice = mBtAdapter.getRemoteDevice(m_address)
                    m_bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(m_myUUID)
                    m_bluetoothSocket!!.connect()
                }
                Toast.makeText(requireActivity(), "Conexión exitosa", Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                Toast.makeText(requireActivity(), "Error de conexión", Toast.LENGTH_LONG).show()
            }
        }

        return root
    }

    private fun goToWeightProducts() {
        val action = BluetoothFragmentDirections.actionBluetoothFragmentToWeightProductsFragment(parsedProducts)
        root.findNavController().navigate(action)
    }
}