/*
 * Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.core

import com.intellij.openapi.externalSystem.service.project.IdeModifiableModelsProviderImpl
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.ResolveScopeEnlarger
import com.intellij.psi.search.SearchScope
import org.jetbrains.kotlin.config.TargetPlatformKind
import org.jetbrains.kotlin.idea.caches.resolve.findImplementingModules
import org.jetbrains.kotlin.idea.project.targetPlatform

class KotlinResolveScopeEnlarger : ResolveScopeEnlarger() {
    override fun getAdditionalResolveScope(file: VirtualFile, project: Project): SearchScope? {
        val moduleManager = ModuleManager.getInstance(project)
        val module = moduleManager.modules.find {
            it.targetPlatform == TargetPlatformKind.Common && it.moduleScope.contains(file)
        } ?: return null

        val implementingModule = module.findImplementingModules(IdeModifiableModelsProviderImpl(project)).find {
            it.targetPlatform is TargetPlatformKind.Jvm
        } ?: return null
        return implementingModule.getModuleRuntimeScope(false)
    }
}