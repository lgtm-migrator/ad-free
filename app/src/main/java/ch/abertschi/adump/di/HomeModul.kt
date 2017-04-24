/*
 * Ad Free
 * Copyright (c) 2017 by abertschi, www.abertschi.ch
 * See the file "LICENSE" for the full license governing this code.
 */

package ch.abertschi.adump.di

import android.content.Context
import ch.abertschi.adump.model.PreferencesFactory
import ch.abertschi.adump.presenter.HomePresenter
import ch.abertschi.adump.view.home.HomeView

/**
 * Created by abertschi on 15.04.17.
 */

class HomeModul(val context: Context, val homeView: HomeView) {

    fun provideControlPresenter(): HomePresenter {
        return HomePresenter(homeView, PreferencesFactory.providePrefernecesFactory(context))
    }

}
