package co.teltech.base.ui.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import co.teltech.base.R

class MessageDialogFragment : DialogFragment() {
    private var message: String? = null
    private var onOkButtonClickListener: OnOkButtonClickListener? = null
    private var customOkMessage: String? = null

    companion object {
        fun newInstance(
            message: String,
            customOkMessage: String?,
            onOkButtonClickListener: OnOkButtonClickListener?
        ): MessageDialogFragment {
            val fragment = MessageDialogFragment()
            val args = Bundle()
            args.putString("DIALOG_MESSAGE", message)
            args.putString("CUSTOM_OK_MESSAGE", customOkMessage)
            fragment.setOnOkClickListener(onOkButtonClickListener)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        message = arguments?.getString("DIALOG_MESSAGE")
        customOkMessage = arguments?.getString("CUSTOM_OK_MESSAGE")
    }
    fun setOnOkClickListener(onOkButtonClickListener: OnOkButtonClickListener?){
        this.onOkButtonClickListener = onOkButtonClickListener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val view = inflater.inflate(R.layout.fragment_message_dialog, container, false)
        val messageDialogOkButton = view.findViewById<View>(R.id.messageDialogOkButton) as Button
         val messageDialogTitle = view.findViewById<View>(R.id.messageDialogTitle) as TextView
        messageDialogTitle.text = message

        if (onOkButtonClickListener == null)
            messageDialogOkButton.setOnClickListener { dismiss() }
        else
            messageDialogOkButton.setOnClickListener { onOkButtonClickListener?.onOkClick() }

        if (customOkMessage != null) {
            messageDialogOkButton.text = customOkMessage
        }
        return view
    }

    interface OnOkButtonClickListener {
        fun onOkClick()
    }
}