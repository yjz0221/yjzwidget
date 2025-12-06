package com.github.yjz.widget.base


import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.github.yjz.widget.util.KtScreenUtils


/**
 * 描述: 基类DialogFragment
 */
abstract class BaseDialogFragment(@LayoutRes private val layoutId: Int) :
    DialogFragment(), DialogInterface.OnKeyListener {

    lateinit var mContext: Context
        private set

    private var loaded: Boolean = false

    var isShowing: Boolean = false
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(layoutId, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            it.window?.decorView?.setPadding(0, 0, 0, 0)
            it.setCancelable(cancelable())
            it.setCanceledOnTouchOutside(canceledOnTouchOutside())
            it.setOnKeyListener(this)
        }

        initView(view,savedInstanceState)
        initData()
        observerLiveData()
    }

    abstract fun initView(view: View,savedInstanceState: Bundle?)

    open fun initData() {}

    open fun observerLiveData() {}

    override fun onStart() {
        super.onStart()

        dialog?.window?.let {
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            if (getAnimation() > 0)
                it.setWindowAnimations(getAnimation())

            val lp = it.attributes
            lp.width = getWidth()
            lp.height = getHeight()
            lp.y = getOffsetY()
            lp.dimAmount = getDimAmount()
            lp.gravity = getGravity()

            it.attributes = lp
        }
    }

    override fun onResume() {
        super.onResume()
        isShowing = true

        if (!loaded) {
            loaded = true
            lazyLoad()
        }
    }

    open fun lazyLoad() {}

    open fun getWidth(): Int = (KtScreenUtils.getRealScreenWidth(mContext) * 0.8).toInt()

    open fun getHeight(): Int = WindowManager.LayoutParams.WRAP_CONTENT

    /**
     * 距离底部的偏移距离
     */
    open fun getOffsetY(): Int = 0

    open fun getDimAmount(): Float = 0.6F

    open fun getGravity(): Int = Gravity.CENTER

    open fun getAnimation(): Int = 0

    open fun cancelable(): Boolean = true

    open fun canceledOnTouchOutside(): Boolean = false

    override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_DOWN) {
            if (cancelable()) {
                dismissAllowingStateLoss()
                return true
            }
        }

        return false
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            val ft = manager.beginTransaction()
            ft.add(this, tag)
            ft.commitAllowingStateLoss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        isShowing = false
        super.onDismiss(dialog)
    }

    override fun onDestroyView() {
        loaded = false
        super.onDestroyView()
    }

}