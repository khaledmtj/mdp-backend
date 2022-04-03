package com.grp11mdp.ArabicSpellCheck

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ScrollView
import android.widget.TextView

class MyScrollView(context: Context, attrs: AttributeSet) : ScrollView(context, attrs) {

    private var myTextView: TextView
    private var myScrollerId: ScrollView

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.my_scroll_view, this, true)
        myTextView = view.findViewById(R.id.myTextView)
        myScrollerId = view.findViewById(R.id.MY_SCROLLER_ID)
    }

    
}