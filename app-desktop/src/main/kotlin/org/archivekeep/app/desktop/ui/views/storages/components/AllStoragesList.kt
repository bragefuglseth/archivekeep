package org.archivekeep.app.desktop.ui.views.storages.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import compose.icons.TablerIcons
import compose.icons.tablericons.DotsVertical
import org.archivekeep.app.desktop.ui.components.LoadableGuard
import org.archivekeep.app.desktop.ui.components.richcomponents.StorageDropdownIconLaunched
import org.archivekeep.app.desktop.ui.designsystem.sections.SectionCard
import org.archivekeep.app.desktop.ui.designsystem.sections.SectionCardBottomList
import org.archivekeep.app.desktop.ui.designsystem.sections.SectionCardTitle
import org.archivekeep.app.desktop.ui.designsystem.sections.sectionCardHorizontalPadding
import org.archivekeep.app.desktop.ui.views.storages.StoragesVM
import org.archivekeep.utils.loading.Loadable

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllStoragesList(allStorages: Loadable<List<StoragesVM.Storage>>) {
    LoadableGuard(allStorages) { allLocalArchives ->
        VerticalGrid(
            columns = SimpleGridCells.Adaptive(minSize = 240.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            allLocalArchives.forEach { storage ->
                SectionCard {
                    SectionCardTitle(
                        // TODO
                        false,
                        storage.displayName,
                        icons = {
                            StorageDropdownIconLaunched(storage.uri)
                        },
                    )

                    Spacer(Modifier.height(4.dp))

                    SectionCardBottomList(storage.repositoriesInThisStorage) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        vertical = 4.dp,
                                        horizontal = sectionCardHorizontalPadding,
                                    ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                            ) {
                                val name = it.displayName

                                Text(
                                    name,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false,
                                    fontSize = 14.sp,
                                    lineHeight = 16.sp,
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(Modifier.padding(6.dp)) {
                                    Icon(
                                        TablerIcons.DotsVertical,
                                        contentDescription = "Upload",
                                        Modifier.size(16.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
