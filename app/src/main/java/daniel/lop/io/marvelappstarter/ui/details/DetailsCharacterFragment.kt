package daniel.lop.io.marvelappstarter.ui.details

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import dagger.hilt.android.AndroidEntryPoint
import daniel.lop.io.marvelappstarter.R
import daniel.lop.io.marvelappstarter.data.model.character.CharacterModel
import daniel.lop.io.marvelappstarter.databinding.FragmentDetailsCharacterBinding
import daniel.lop.io.marvelappstarter.ui.adapters.ComicAdapter
import daniel.lop.io.marvelappstarter.ui.base.BaseFragment
import daniel.lop.io.marvelappstarter.ui.components.DescriptionDetailsBottomSheet
import daniel.lop.io.marvelappstarter.ui.state.ResourceState
import daniel.lop.io.marvelappstarter.util.hide
import daniel.lop.io.marvelappstarter.util.isVisible
import daniel.lop.io.marvelappstarter.util.limitDescription
import daniel.lop.io.marvelappstarter.util.loadImage
import daniel.lop.io.marvelappstarter.util.show
import daniel.lop.io.marvelappstarter.util.showAlertSnackbar
import daniel.lop.io.marvelappstarter.util.showErrorSnackbar
import daniel.lop.io.marvelappstarter.util.showSuccessSnackbar
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class DetailsCharacterFragment :
    BaseFragment<FragmentDetailsCharacterBinding, DetailsCharacterViewModel>() {
    override val viewModel: DetailsCharacterViewModel by viewModels()

    private val args: DetailsCharacterFragmentArgs by navArgs()
    private val comicAdapter by lazy { ComicAdapter() }
    private lateinit var characterModel: CharacterModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        characterModel = args.character
        viewModel.fetch(characterModel.id)
        setupRecycleView()
        onLoadedCharacter(characterModel)
        collectObserver()
        descriptionCharacter()

        if (activity is AppCompatActivity) {
            (activity as AppCompatActivity).setSupportActionBar(binding.tbDetails)
        }
        binding.fab.setOnClickListener { view ->
            viewModel.insert(characterModel)
            showSuccessSnackbar(
                requireContext().getString(R.string.saved_successfully),
                binding.layoutDetails
            )
        }
        loadImage(
            binding.tbImage,
            characterModel.thumbnailModel.path,
            characterModel.thumbnailModel.extension
        )
        binding.tbDetails.title = characterModel.name

        binding.appBar.addOnOffsetChangedListener(object : OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true
                    binding.tbDetails.background = ColorDrawable(
                        ContextCompat.getColor(requireContext(), R.color.toolbarCollapsed)
                    )
                } else if (isShow) {
                    isShow = false
                    binding.tbDetails.background = ColorDrawable(
                        ContextCompat.getColor(requireContext(), R.color.toolbarExpanded)
                    )
                }
            }
        })
    }

    private fun descriptionCharacter() {
        binding.layoutScrollabeDetails.tvDescriptionCharacterDetails.setOnClickListener {
            onShowDialog(characterModel)
        }
    }

    private fun onShowDialog(characterModel: CharacterModel) {
        val descriptionDetailsBottomSheet = DescriptionDetailsBottomSheet(
            context = requireContext(),
            comics = characterModel
        )
        descriptionDetailsBottomSheet.show()
    }

    private fun collectObserver() = lifecycleScope.launch {
        viewModel.details.collect { result ->
            when (result) {
                is ResourceState.Success -> {
                    binding.layoutScrollabeDetails.progressBarDetail.hide()
                    result.data?.let { values ->
                        if (values.data.result.isNotEmpty()) {
                            comicAdapter.comics = values.data.result.toList()
                        } else {
                            showAlertSnackbar(
                                getString(R.string.empty_list_comics),
                                binding.layoutDetails
                            )
                        }
                    }
                }

                is ResourceState.Error -> {
                    binding.layoutScrollabeDetails.progressBarDetail.hide()
                    result.message?.let { message ->
                        Timber.tag("DetailsCharacter").e("Error -> $message")
                        showErrorSnackbar(message, binding.layoutDetails)
                    }
                }

                is ResourceState.Loading -> {
                    binding.layoutScrollabeDetails.progressBarDetail.show()
                }

                else -> {
                }
            }
        }
    }

    private fun onLoadedCharacter(characterModel: CharacterModel) = with(binding) {
        if (characterModel.description.isEmpty()) {
            layoutScrollabeDetails.tvDescriptionCharacterDetails.text =
                requireContext().getString(R.string.text_description_empty)
        } else {
            layoutScrollabeDetails.tvDescriptionCharacterDetails.text =
                characterModel.description.limitDescription(100)
            layoutScrollabeDetails.textViewInfo.isVisible(characterModel.description.length > 100)
            layoutScrollabeDetails.textViewInfo.setOnClickListener {
                onShowDialog(characterModel)
            }
        }
    }

    private fun setupRecycleView() = with(binding) {
        layoutScrollabeDetails.rvComics.apply {
            adapter = comicAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailsCharacterBinding =
        FragmentDetailsCharacterBinding.inflate(inflater, container, false)
}