package com.univpm.pinpointmvvm.view.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.univpm.pinpointmvvm.R
import com.univpm.pinpointmvvm.databinding.FragmentCurrentProfileBinding
import com.univpm.pinpointmvvm.utils.ImageLoadListener
import com.univpm.pinpointmvvm.utils.SnackbarManager
import com.univpm.pinpointmvvm.uistate.UserUiState
import com.univpm.pinpointmvvm.view.activities.AccountEditActivity
import com.univpm.pinpointmvvm.view.activities.AccountSettingsActivity
import com.univpm.pinpointmvvm.view.adapter.CurrentUserPostAdapter
import com.univpm.pinpointmvvm.viewmodel.CurrentProfileViewModel
import kotlinx.coroutines.launch

/**
 * Fragment per la visualizzazione del profilo utente corrente
 */
class CurrentProfileFragment : Fragment(), ImageLoadListener {
    companion object {
        fun newInstance() = CurrentProfileFragment()
        private const val POST_SUCCESSFULLY_DELETED = "Il post è stato correttamente cancellato"
        private const val POST_UNSUCCESSFULLY_DELETED =
            "Il post non è stato correttamente cancellato"
    }


    private val viewModel: CurrentProfileViewModel by viewModels()
    private lateinit var viewBinding: FragmentCurrentProfileBinding
    private val currentUserPostAdapter: CurrentUserPostAdapter by lazy {
        CurrentUserPostAdapter(deleteListener = {
            viewModel.deletePost(it)
        }, positionListener = {
            viewModel.viewOnGoogleMap(it, requireContext())
        }, imageLoadListener = this
        )
    }
    private var numImagesLoaded = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentCurrentProfileBinding.inflate(inflater, container, false).apply {
            editProfileButton.setOnClickListener {
                startActivity(Intent(context, AccountEditActivity::class.java))
            }
            profileFragmentSettings.setOnClickListener {
                requireActivity().finish()
                startActivity(Intent(context, AccountSettingsActivity::class.java))
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                setupUi(uiState)
                observeListOfPosts(uiState)
            }
        }
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeDeletePostSuccess()
        observeDeletePostError()
    }

    /**
     * Metodo per ottenere il colore del tema corrente
     * @return Int colore del tema corrente
     */
    private fun getColorTheme(): Int {
        val currentNightMode = AppCompatDelegate.getDefaultNightMode()
        return if (currentNightMode == AppCompatDelegate.MODE_NIGHT_YES) {
            ContextCompat.getColor(requireContext(), R.color.primaryNight)
        } else {
            ContextCompat.getColor(requireContext(), R.color.primaryLight)
        }
    }

    /**
     * Metodo per osservare la lista dei post dell'utente corrente
     * @param uiState UserUiState stato dell'utente
     */
    private fun observeListOfPosts(uiState: UserUiState) {
        uiState.posts?.observe(viewLifecycleOwner) { posts ->
            if (posts.isEmpty()) {
                viewBinding.progressBarCurrentProfile.visibility = View.GONE
                viewBinding.appBarLayoutProfile.visibility = View.VISIBLE
                viewBinding.nestedScrollViewProfile.visibility = View.VISIBLE
                viewBinding.noPostsTextView.visibility = View.VISIBLE
                viewBinding.postRecyclerView.visibility = View.GONE
            } else {

                for (post in posts) {
                    post.username = uiState.username
                    post.userPic = uiState.image
                }
                currentUserPostAdapter.posts = posts
                viewBinding.totalPosts.text = posts.size.toString()
                currentUserPostAdapter.notifyDataSetChanged()
            }
        }
    }

    /**
     * Metodo per impostare l'interfaccia grafica
     * @param uiState UserUiState stato dell'utente
     */
    private fun setupUi(uiState: UserUiState) {
        viewBinding.apply {
            profileFragmentUsername.text = uiState.username
            profileFragmentFullName.text = uiState.fullname
            profileFragmentFullName.text = uiState.fullname
            profileFragmentBio.text = uiState.bio
            profileFragmentProfileImage.avatarBorderColor = getColorTheme()
            profileFragmentProfileImage.load(uiState.image) {
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
                transformations(CircleCropTransformation())
            }
            postRecyclerView.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = currentUserPostAdapter
            }
        }
        uiState.followers?.observe(viewLifecycleOwner) { followers ->
            viewBinding.totalFollowers.text = followers.toString()
        }
        uiState.following?.observe(viewLifecycleOwner) { following ->
            viewBinding.totalFollowing.text = following.toString()
        }
    }

    /**
     * Metodo per osservare l'errore di cancellazione di un post
     */
    private fun observeDeletePostError() {
        lifecycleScope.launch {
            viewModel.postDeleteError.collect {
                if (it.isNotBlank()) {
                    val error = "$POST_UNSUCCESSFULLY_DELETED: \n$it"
                    SnackbarManager.onFailure(error, this@CurrentProfileFragment)
                }
            }

        }
    }

    /**
     * Metodo per osservare il successo di cancellazione di un post
     */
    private fun observeDeletePostSuccess() {
        lifecycleScope.launch {
            viewModel.postDeleteSuccess.collect {
                if (it) {
                    SnackbarManager.onSuccess(
                        POST_SUCCESSFULLY_DELETED, this@CurrentProfileFragment
                    )
                }
            }

        }

    }

    /**
     * Metodo per notificare il caricamento di un'immagine e cambiare la visibilità della progress bar
     */
    override fun onImageLoaded() {
        numImagesLoaded++
        if (numImagesLoaded >= currentUserPostAdapter.itemCount) {
            viewBinding.progressBarCurrentProfile.visibility = View.GONE
            viewBinding.appBarLayoutProfile.visibility = View.VISIBLE
            viewBinding.nestedScrollViewProfile.visibility = View.VISIBLE
        }
    }
}