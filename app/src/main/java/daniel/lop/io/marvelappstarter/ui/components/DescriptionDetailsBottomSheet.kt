package daniel.lop.io.marvelappstarter.ui.components

import android.content.Context
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import daniel.lop.io.marvelappstarter.R
import daniel.lop.io.marvelappstarter.data.model.character.CharacterModel
import daniel.lop.io.marvelappstarter.util.loadImage

class DescriptionDetailsBottomSheet constructor(
    context: Context,
    comics: CharacterModel
) : BottomSheetDialog(context) {

    private val openedComic = comics
    private val sheetView = layoutInflater.inflate(R.layout.layout_description_dialog, null)
    private val image = sheetView.findViewById(R.id.ivBottomSheetDescription) as ImageView
    private val description = sheetView.findViewById(R.id.tvBottomSheetDescription) as TextView
    private val closeButton = sheetView.findViewById(R.id.btnBottomSheetDismiss) as Button

    override fun show() {
        setContentView(sheetView)
        val bottomSheet = window?.findViewById(R.id.design_bottom_sheet) as FrameLayout
        bottomSheet.setBackgroundResource(R.drawable.shape_layout_corner)
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        loadImage(
            imageView = image,
            path = openedComic.thumbnailModel.path,
            extension = openedComic.thumbnailModel.extension
        )

        if (openedComic.description.isNotEmpty()) description.text = openedComic.description
        else description.text = context.getText(R.string.text_description_empty)

        closeButton.setOnClickListener {
            this.dismiss()
        }
        super.show()
    }
}
