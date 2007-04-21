/*
 * This file is part of GatorMail, a web based email client.
 *
 * Copyright (C) 2007 The Open Systems Group / University of Florida
 *
 * GatorMail is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * GatorMail is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GatorMail; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package edu.ufl.osg.gatormail.client.ui.messageList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import edu.ufl.osg.gatormail.client.GatorMailWidget;
import edu.ufl.osg.gatormail.client.model.GMAddress;
import edu.ufl.osg.gatormail.client.model.GMFlags;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.model.message.GMMessageHeaders;
import edu.ufl.osg.gatormail.client.model.message.GMMessageSummary;
import edu.ufl.osg.gatormail.client.services.MessageListService;
import edu.ufl.osg.gatormail.client.services.MessageListServiceAsync;
import edu.ufl.osg.gatormail.client.services.MessageService;
import edu.ufl.osg.gatormail.client.services.MessageServiceAsync;
import edu.ufl.osg.gatormail.client.ui.FlaggedLabel;
import edu.ufl.osg.gatormail.client.ui.FromAddressesLabel;
import edu.ufl.osg.gatormail.client.ui.ReceivedDateLabel;
import edu.ufl.osg.gatormail.client.ui.SelectionCheckBox;
import edu.ufl.osg.gatormail.client.ui.SubjectLabel;
import org.mcarthur.sandy.gwt.event.list.client.EventList;
import org.mcarthur.sandy.gwt.event.list.client.EventLists;
import org.mcarthur.sandy.gwt.event.list.client.FilteredEventList;
import org.mcarthur.sandy.gwt.event.list.client.ListEvent;
import org.mcarthur.sandy.gwt.event.list.client.ListEventListener;
import org.mcarthur.sandy.gwt.event.list.client.RangedEventList;
import org.mcarthur.sandy.gwt.table.client.ObjectListTable;
import org.mcarthur.sandy.gwt.table.client.TableBodyGroup;
import org.mcarthur.sandy.gwt.table.client.TableCell;
import org.mcarthur.sandy.gwt.table.client.TableCol;
import org.mcarthur.sandy.gwt.table.client.TableDataCell;
import org.mcarthur.sandy.gwt.table.client.TableFooterGroup;
import org.mcarthur.sandy.gwt.table.client.TableHeaderCell;
import org.mcarthur.sandy.gwt.table.client.TableHeaderGroup;
import org.mcarthur.sandy.gwt.table.client.TableRow;
import org.mcarthur.sandy.gwt.table.client.TableRowGroup;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Display a list of messages in a folder.
 *
 * @author Sandy McArthur
 */
public class MessageList extends Composite {
    private static final Command NOT_IMPLEMENTED_COMMAND = new NotImplementedCommand();
    private static final NotImplementedClickListener NOT_IMPLEMENTED_CLICK_LISTENER = new NotImplementedClickListener();

    private final GatorMailWidget client;
    private final GMFolder folder;

    private final VerticalPanel vp = new VerticalPanel();
    private final ObjectListTable olt;
    private final MessageRenderer oltRenderer;
    private final ObjectListTable oltSummary;
    private final SummaryMessageRenderer oltSummaryRenderer;

    private final EventList/*<GMMessage>*/ messages = EventLists.eventList();
    private final EventList/*<GMMessage>*/ messagesReversed = EventLists.reverseEventList(messages);
    private final FilteredEventList/*<GMMessage>*/ messagesFiltered = EventLists.filteredEventList(messagesReversed);
    private final RangedEventList/*<GMMessage>*/ messagesPaged = EventLists.rangedEventList(messagesFiltered, 25);
    private final EventList/*<GMMessage>*/ messagesView = messagesPaged;

    private final EventList/*<GMMessage>*/ selectedMessages = EventLists.eventList();
    private final ListEventListener selectedMessagesListener = new SelectedMessagesListEventLisener();

    private boolean refreshing = false;

    public MessageList(final GatorMailWidget client, final GMFolder gmFolder) {
        this.client = client;
        this.folder = gmFolder;

        initWidget(vp);
        addStyleName("gm-MessageList");

        // Top bar
        final HorizontalPanel headerRow = new HorizontalPanel();
        headerRow.addStyleName("gm-MessageList-HeaderRow");
        headerRow.setWidth("100%");

        final MessageListFolderNameLabel folderNameLabel = new MessageListFolderNameLabel(folder);
        headerRow.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
        headerRow.add(folderNameLabel);
        headerRow.setCellWidth(folderNameLabel, "100%");


        final TabBar viewTabs = new TabBar();
        viewTabs.addStyleName("gm-MessageList-ViewTabs");

        viewTabs.addTab("List");
        viewTabs.addTab("Summaries");
        viewTabs.selectTab(0);
        viewTabs.addTabListener(new TabListener() {
            public boolean onBeforeTabSelected(final SourcesTabEvents sender, final int tabIndex) {
                return true;
            }

            public void onTabSelected(final SourcesTabEvents sender, final int tabIndex) {
                if (tabIndex == 0) {
                    final int idx = vp.getWidgetIndex(oltSummary);
                    if (idx > -1) {
                        vp.insert(olt, idx);
                        vp.remove(oltSummary);
                    }
                } else if (tabIndex == 1) {
                    final int idx = vp.getWidgetIndex(olt);
                    if (idx > -1) {
                        vp.insert(oltSummary, idx);
                        vp.remove(olt);
                    }

                } else {
                    throw new IllegalStateException("Unexpected tabIndex: " + tabIndex);
                }
            }
        });

        headerRow.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
        headerRow.add(viewTabs);

        vp.add(headerRow);


        // Top buttons
        final HorizontalPanel buttonsRow = new HorizontalPanel();
        buttonsRow.addStyleName("gm-MessageList-ButtonsRow");
        buttonsRow.setWidth("100%");

        final HorizontalPanel buttons = new HorizontalPanel();

        if (!folder.getFullName().equals(client.getAccount().getTrashFolderName())
                && !folder.getFullName().equals(client.getAccount().getTrashFolderName())) {
            // not Trash folder
            final Button delete = new Button("Delete");
            delete.setEnabled(!selectedMessages.isEmpty());
            selectedMessages.addListEventListener(new ListEventListener() {
                public void listChanged(final ListEvent listEvent) {
                    delete.setEnabled(!selectedMessages.isEmpty());
                }
            });
            delete.addClickListener(new ClickListener() {
                public void onClick(final Widget sender) {
                    assert !selectedMessages.isEmpty() : "No messages selected.";
                    final List messagesToDelete = new ArrayList(selectedMessages);
                    final MessageServiceAsync service = MessageService.App.getInstance();
                    service.deleteMessages(client.getAccount(), messagesToDelete, new AsyncCallback() {
                        public void onSuccess(final Object result) {
                            final MessageService.DeleteMessagesResponse response = (MessageService.DeleteMessagesResponse)result;
                            // TODO: handle delete response
                            refresh();
                        }

                        public void onFailure(final Throwable caught) {
                            GWT.log("Failed to delete messages.", caught);
                        }
                    });
                }
            });
            buttons.add(delete);
        } else {
            // Trash or Junk folder
            final Button deleteForever = new Button("Delete Forever");
            deleteForever.setEnabled(!selectedMessages.isEmpty());
            selectedMessages.addListEventListener(new ListEventListener() {
                public void listChanged(final ListEvent listEvent) {
                    deleteForever.setEnabled(!selectedMessages.isEmpty());
                }
            });
            deleteForever.addClickListener(new ClickListener() {
                public void onClick(final Widget sender) {
                    assert !selectedMessages.isEmpty() : "No messages selected.";
                    final List messagesToDelete = new ArrayList(selectedMessages);
                    final MessageServiceAsync service = MessageService.App.getInstance();
                    service.deleteMessagesForever(client.getAccount(), messagesToDelete, new AsyncCallback() {
                        public void onSuccess(final Object result) {
                            final MessageService.DeleteMessagesResponse response = (MessageService.DeleteMessagesResponse)result;
                            // TODO: handle delete response
                            refresh();
                        }

                        public void onFailure(final Throwable caught) {
                            GWT.log("Failed to delete messages forever.", caught);
                        }
                    });
                }
            });
            buttons.add(deleteForever);
        }

        final Button reportSpam = new Button("Report Spam");
        reportSpam.setEnabled(false);
        reportSpam.addClickListener(NOT_IMPLEMENTED_CLICK_LISTENER);
        buttons.add(reportSpam);

        final Button moveCopy = new Button("Move/Copy");
        moveCopy.setEnabled(!selectedMessages.isEmpty());
        selectedMessages.addListEventListener(new ListEventListener() {
            public void listChanged(final ListEvent listEvent) {
                moveCopy.setEnabled(!selectedMessages.isEmpty());
            }
        });
        moveCopy.addClickListener(NOT_IMPLEMENTED_CLICK_LISTENER);
        buttons.add(moveCopy);

        final Label show = new Label("Show:");
        buttons.add(show);


        final MenuBar showMenu = new MenuBar();
        final MenuBar showMenuPopup = new MenuBar(true);
        showMenuPopup.addItem("All", new AllFilterCommand());

        final MenuBar flaggedMenuPopup = new MenuBar(true);
        flaggedMenuPopup.addItem("Flagged", new HasFlagFilterCommand(GMFlags.GMFlag.FLAGGED));
        flaggedMenuPopup.addItem("Not Flagged", new NotFlagFilterCommand(GMFlags.GMFlag.FLAGGED));
        showMenuPopup.addItem("Flagged", flaggedMenuPopup);

        showMenuPopup.addItem("Unread", new NotFlagFilterCommand(GMFlags.GMFlag.SEEN));
        showMenuPopup.addItem("Read", new HasFlagFilterCommand(GMFlags.GMFlag.SEEN));

        showMenuPopup.addItem("From", createFromMenuPopup());

        final MenuBar toMenuPopup = new MenuBar(true);
        toMenuPopup.addItem("To: me", new ToMeFilterCommand());
        toMenuPopup.addItem("To, CC: me", new ToCcMeFilterCommand());
        toMenuPopup.addItem("Not me", new NotToCcMeFilterCommand());
        toMenuPopup.addItem("Someone Else...", NOT_IMPLEMENTED_COMMAND);
        showMenuPopup.addItem("To", toMenuPopup);

        showMenuPopup.addItem("Deleted", new HasFlagFilterCommand(GMFlags.GMFlag.DELETED));

        showMenu.addItem("[Pick one]", showMenuPopup);
        buttons.add(showMenu);

        buttonsRow.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
        buttonsRow.add(buttons);

        vp.add(buttonsRow);

        // Select row
        final HorizontalPanel selectRow = new HorizontalPanel();
        selectRow.addStyleName("gm-MessageList-SelectRow");
        selectRow.setWidth("100%");

        final HorizontalPanel selectChoices = new HorizontalPanel();
        selectChoices.setSpacing(2);

        selectChoices.add(new Label("Select:"));
        final Hyperlink selectAll = new Hyperlink("All", null);
        selectAll.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                // XXX: optimize
                selectedMessages.clear();
                selectedMessages.addAll(messagesView);
            }
        });
        selectChoices.add(selectAll);
        final Hyperlink selectNone = new Hyperlink("None", null);
        selectNone.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                selectedMessages.clear();
            }
        });
        selectChoices.add(selectNone);
        final Hyperlink selectFlagged = new Hyperlink("Flagged", null);
        selectFlagged.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                selectedMessages.clear();
                final Iterator iter = messagesView.iterator();
                while (iter.hasNext()) {
                    final GMMessage message = (GMMessage)iter.next();
                    if (message.getFlags() != null) {
                        if (message.getFlags().contains(GMFlags.GMFlag.FLAGGED)) {
                            selectedMessages.add(message);
                        }
                    } else {
                        // TODO: fetch flags
                    }
                }
            }
        });
        selectChoices.add(selectFlagged);
        final Hyperlink selectUnread = new Hyperlink("Unread", null);
        selectUnread.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                selectedMessages.clear();
                final Iterator iter = messagesView.iterator();
                while (iter.hasNext()) {
                    final GMMessage message = (GMMessage)iter.next();
                    if (message.getFlags() != null) {
                        if (!message.getFlags().contains(GMFlags.GMFlag.SEEN)) {
                            selectedMessages.add(message);
                        }
                    } else {
                        // TODO: fetch flags
                    }
                }
            }
        });
        selectChoices.add(selectUnread);
        final Hyperlink selectRead = new Hyperlink("Read", null);
        selectRead.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                selectedMessages.clear();
                final Iterator iter = messagesView.iterator();
                while (iter.hasNext()) {
                    final GMMessage message = (GMMessage)iter.next();
                    if (message.getFlags() != null) {
                        if (message.getFlags().contains(GMFlags.GMFlag.SEEN)) {
                            selectedMessages.add(message);
                        }
                    } else {
                        // TODO: fetch flags
                    }
                }
            }
        });
        selectChoices.add(selectRead);

        final MenuBar fromMenu = new MenuBar();
        fromMenu.addItem("From", createFromMenuPopup());

        selectChoices.add(fromMenu);

        selectRow.add(selectChoices);

        final Button refresh = new Button("[Update Messages]");
        refresh.addClickListener(new ClickListener() {
            public void onClick(final Widget sender) {
                refresh();
            }
        });
        selectRow.add(refresh);

        vp.add(selectRow);

        buttonsRow.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
        buttonsRow.add(new MessageListPageLabel(this, messagesPaged));
        selectRow.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
        selectRow.add(new MessageListPageButtons(messagesPaged));

        // Message List
        oltRenderer = new MessageRenderer();
        olt = new ObjectListTable(oltRenderer, messagesView);
        setAttribute(olt.getElement(), "width", "100%");
        setAttribute(olt.getElement(), "cellspacing", "0");
        setAttribute(olt.getElement(), "cellpadding", "0");
        //setAttribute(olt.getElement(), "border", "1");

        olt.addStyleName("gm-MessageList-Table");
        
        vp.add(olt);

        // TODO: do this better
        oltSummaryRenderer = new SummaryMessageRenderer();
        oltSummary = new ObjectListTable(oltSummaryRenderer, messagesView);
        setAttribute(oltSummary.getElement(), "width", "100%");
        setAttribute(oltSummary.getElement(), "cellspacing", "0");
        setAttribute(oltSummary.getElement(), "cellpadding", "0");
        //setAttribute(oltSummary.getElement(), "border", "1");

        oltSummary.addStyleName("gm-MessageList-Message-Summary");

        DeferredCommand.add(new FetchMessageListCommand());
    }

    private void refresh() {
        if (!refreshing) {
            refreshing = true;
            final MessageListServiceAsync service = MessageListService.App.getInstance();
            final long startUID;
            final long endUID;
            if (messages.size() > 0) {
                final GMMessage first = (GMMessage)messages.get(0);
                final GMMessage last = (GMMessage)messages.get(messages.size()-1);
                startUID = first.getUid();
                endUID = last.getUid();
            } else {
                startUID = endUID = 0;
            }
            service.fetchMessageListChanges(client.getAccount(), folder, startUID, endUID, messages.size(), new AsyncCallback() {
                public void onSuccess(final Object result) {
                    refreshing = false;
                    final MessageListService.MessageListUpdate update = (MessageListService.MessageListUpdate)result;
                    assert update.getRequestedStartUID() == startUID;
                    assert update.getRequestedEndUID() == endUID;

                    if (update.getValidUIDs() != null) {
                        final long[] uids = update.getValidUIDs();

                        final Iterator iter = messages.iterator();
                        while (iter.hasNext()) {
                            final GMMessage message = (GMMessage)iter.next();
                            if (binarySearch(uids, message.getUid()) < 0) {
                                iter.remove();
                            }
                        }
                    }
                    if (update.getBeforeStart() != null && !update.getBeforeStart().isEmpty()) {
                        messages.addAll(0, update.getBeforeStart());
                    }
                    if (update.getAfterEnd() != null && !update.getAfterEnd().isEmpty()) {
                        messages.addAll(update.getAfterEnd());
                    }


                }
                public void onFailure(final Throwable caught) {
                    refreshing = false;
                    GWT.log("MessageList.refresh) failed!", caught);
                }
            });
        }
    }

    public static int binarySearch(final long[] a, final long key) {
        int low = 0;
        int high = a.length - 1;

        while (low <= high) {
            final int mid = low + ((high - low) / 2);
            final long midVal = a[mid];

            if (midVal < key) {
                low = mid + 1;
            } else if (midVal > key) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1);  // key not found.
    }

    private MenuBar createFromMenuPopup() {
        final MenuBar fromMenuDropDown = new MenuBar(true);
        fromMenuDropDown.addItem("In Address Book", NOT_IMPLEMENTED_COMMAND);
        fromMenuDropDown.addItem("Address Book Group", NOT_IMPLEMENTED_COMMAND);
        fromMenuDropDown.addItem("Custom ...", NOT_IMPLEMENTED_COMMAND);
        return fromMenuDropDown;
    }

    private class FetchMessageListCommand implements Command {
        public void execute() {
            final MessageListServiceAsync service = MessageListService.App.getInstance();

            service.fetchMessages(client.getAccount(), folder, new AsyncCallback() {
                public void onSuccess(final Object result) {
                    messages.addAll((List)result);
                }

                public void onFailure(final Throwable caught) {
                    GWT.log("FetchMessageListCommand", caught);
                }
            });
        }
    }

    private class MessageRenderer implements ObjectListTable.Renderer, ObjectListTable.AttachRenderer, ObjectListTable.ColSpecRenderer {
        public void render(final Object obj, final TableBodyGroup rowGroup) {
            final GMMessage message = (GMMessage)obj;

            rowGroup.addStyleName("gm-MessageList-tbody");

            final TableRow tr = rowGroup.newTableRow();
            tr.addStyleName("gm-MessageList-tr-headers");

            {
                final TableDataCell checkboxCell = tr.newTableDataCell();
                checkboxCell.addStyleName("gm-MessageList-td");
                checkboxCell.setWidth("25px");
                checkboxCell.add(new SelectionCheckBox(selectedMessages, message));
                tr.add(checkboxCell);
            }

            {
                final TableDataCell flaggedCell = tr.newTableDataCell();
                flaggedCell.addStyleName("gm-MessageList-td");
                flaggedCell.setWidth("2ex");
                flaggedCell.add(new FlaggedLabel(client, message));
                tr.add(flaggedCell);
            }

            {
                final TableDataCell fromCell = tr.newTableDataCell();
                fromCell.addStyleName("gm-MessageList-td");
                fromCell.setWidth("20ex");
                //fromCell.addStyleName("gm-MessageList-from");
                fromCell.add(new FromAddressesLabel(message));
                tr.add(fromCell);
            }

            {
                final TableDataCell toCell = tr.newTableDataCell();
                toCell.addStyleName("gm-MessageList-td");
                toCell.setWidth("2ex");
                toCell.add(new ToMeLabel(client, message));
                tr.add(toCell);
            }

            {
                final TableDataCell subjectCell = tr.newTableDataCell();
                subjectCell.addStyleName("gm-MessageList-td");
                //subjectCell.addStyleName("gm-MessageList-subject");
                subjectCell.add(new SubjectLabel(message));
                tr.add(subjectCell);
            }

            {
                final TableDataCell attachmentCell = tr.newTableDataCell();
                attachmentCell.addStyleName("gm-MessageList-td");
                attachmentCell.setWidth("2ex");
                if (false) {
                    attachmentCell.add(new Image("attachment.png"));
                } else {
                    attachmentCell.add(new HTML("&nbsp;"));
                }
                tr.add(attachmentCell);
            }

            {
                final TableDataCell dateCell = tr.newTableDataCell();
                dateCell.addStyleName("gm-MessageList-td");
                dateCell.setWidth("9.5ex");
                //dateCell.addStyleName("gm-MessageList-date");
                dateCell.add(new ReceivedDateLabel(message));
                tr.add(dateCell);
            }

            rowGroup.add(tr);

            if (message.getHeaders() == null) {
                final MessageServiceAsync service = MessageService.App.getInstance();
                service.fetchHeaders(client.getAccount(), message, new AsyncCallback() {
                    public void onSuccess(final Object result) {
                        message.setHeaders((GMMessageHeaders)result);
                    }

                    public void onFailure(final Throwable caught) {
                        GWT.log("Problem fetching headers", caught);
                    }
                });
            }

            rowGroup.addMouseListener(new TableRowGroup.MouseListener() {
                public void onDblClick(final TableRowGroup rowGroup, final Event event) {
                    client.openMessage(message);
                }

                public void onClick(final TableRowGroup rowGroup, final Event event) {
                }

                public void onMouseDown(final TableRowGroup rowGroup, final Event event) {
                }

                public void onMouseMove(final TableRowGroup rowGroup, final Event event) {
                }

                public void onMouseOver(final TableRowGroup rowGroup, final Event event) {
                }

                public void onMouseOut(final TableRowGroup rowGroup, final Event event) {
                }

                public void onMouseUp(final TableRowGroup rowGroup, final Event event) {
                }
            });
        }

        public void renderHeader(final TableHeaderGroup headerGroup) {
            final TableRow tr = headerGroup.newTableRow();
            final TableHeaderCell checkboxHeader = tr.newTableHeaderCell();
            final TableHeaderCell flaggedHeader = tr.newTableHeaderCell();
            final TableHeaderCell fromHeader = tr.newTableHeaderCell();
            final TableHeaderCell toHeader = tr.newTableHeaderCell();
            final TableHeaderCell subjectHeader = tr.newTableHeaderCell();
            final TableHeaderCell attachmentHeader = tr.newTableHeaderCell();
            final TableHeaderCell dateHeader = tr.newTableHeaderCell();

            checkboxHeader.addStyleName("gm-MessageList-th");
            checkboxHeader.add(new HTML("&nbsp;"));
            checkboxHeader.setWidth("25px");

            flaggedHeader.addStyleName("gm-MessageList-th");
            flaggedHeader.add(new HTML("&nbsp;"));
            flaggedHeader.setWidth("15px");

            fromHeader.addStyleName("gm-MessageList-th");
            fromHeader.add(new Label("From"));
            fromHeader.setWidth("20ex");

            toHeader.addStyleName("gm-MessageList-th");
            toHeader.add(new HTML("&nbsp;"));
            toHeader.setWidth("2ex");

            subjectHeader.addStyleName("gm-MessageList-th");
            subjectHeader.add(new Label("Subject"));

            attachmentHeader.addStyleName("gm-MessageList-th");
            attachmentHeader.add(new HTML("&nbsp;"));
            attachmentHeader.setWidth("15px");

            dateHeader.addStyleName("gm-MessageList-th");
            dateHeader.add(new Label("Sent"));
            dateHeader.setWidth("9.5ex");
            
            tr.add(checkboxHeader);
            tr.add(flaggedHeader);
            tr.add(fromHeader);
            tr.add(toHeader);
            tr.add(subjectHeader);
            tr.add(attachmentHeader);
            tr.add(dateHeader);

            headerGroup.add(tr);
        }

        public void renderFooter(final TableFooterGroup footerGroup) {
            // none
        }

        private final Map/*<GMMessage, PropertyChangeListener>*/ messageToPropertyChangeListeners = new HashMap/*<GMMessage, PropertyChangeListener>*/();

        public void onAttach(final Object obj, final TableBodyGroup rowGroup) {
            final GMMessage message = (GMMessage)obj;
            if (message.getFlags() != null) {
                if (message.getFlags().contains(GMFlags.GMFlag.SEEN)) {
                    rowGroup.addStyleName("gm-MessageList-Message-seen");
                    rowGroup.removeStyleName("gm-MessageList-Message-unseen");
                } else {
                    rowGroup.removeStyleName("gm-MessageList-Message-seen");
                    rowGroup.addStyleName("gm-MessageList-Message-unseen");
                }
            }
            final PropertyChangeListener pcl = new PropertyChangeListener() {
                public void propertyChange(final PropertyChangeEvent evt) {
                }
            };
            messageToPropertyChangeListeners.put(message, pcl);
            message.addPropertyChangeListener("flags", pcl);
        }

        public void onAttach(final TableHeaderGroup rowGroup) {
            // n/a
        }

        public void onAttach(final TableFooterGroup rowGroup) {
            // n/a
        }

        public void onDetach(final Object obj, final TableBodyGroup rowGroup) {
            final GMMessage message = (GMMessage)obj;
            final PropertyChangeListener pcl = (PropertyChangeListener)messageToPropertyChangeListeners.remove(message);
            assert pcl != null;
            message.removePropertyChangeListener(pcl);
        }

        public void onDetach(final TableHeaderGroup rowGroup) {
            // n/a
        }

        public void onDetach(final TableFooterGroup rowGroup) {
            // n/a
        }

        public List getColSpec() {
            final List colSpecs = new ArrayList();

            {
                final TableCol checkboxCol = new TableCol();
                setAttribute(checkboxCol.getElement(), "style", "width:25px;");
                DOM.setStyleAttribute(checkboxCol.getElement(), "width", "25px");
                colSpecs.add(checkboxCol);
            }

            {
                final TableCol flaggedCol = new TableCol();
                setAttribute(flaggedCol.getElement(), "style", "width:15px;");
                DOM.setStyleAttribute(flaggedCol.getElement(), "width", "15px");
                colSpecs.add(flaggedCol);
            }

            {
                final TableCol fromCol = new TableCol();
                //fromCol.addStyleName("gm-MessageList-from-col");
                //fromCol.setWidth("20ex"); // gmail 27ex
                setAttribute(fromCol.getElement(), "style", "width:20ex;");
                DOM.setStyleAttribute(fromCol.getElement(), "width", "20ex");
                colSpecs.add(fromCol);
            }

            {
                final TableCol toCol = new TableCol();
                setAttribute(toCol.getElement(), "style", "width:2ex;");
                DOM.setStyleAttribute(toCol.getElement(), "width", "2ex");
                colSpecs.add(toCol);
            }

            {
                final TableCol subjectCol = new TableCol();
                //subjectCol.addStyleName("gm-MessageList-subject-col");
                colSpecs.add(subjectCol);
            }
            
            {
                final TableCol subjectCol = new TableCol();
                setAttribute(subjectCol.getElement(), "style", "width:15px;");
                DOM.setStyleAttribute(subjectCol.getElement(), "width", "15px");
                colSpecs.add(subjectCol);
            }

            {
                final TableCol dateCol = new TableCol();
                //dateCol.addStyleName("gm-MessageList-date-col");
                //dateCol.setWidth("9.5ex"); // gmail 9.5ex
                setAttribute(dateCol.getElement(), "style", "width:9.5ex;");
                DOM.setStyleAttribute(dateCol.getElement(), "width", "9.5ex");
                colSpecs.add(dateCol);
            }

            return colSpecs;
        }
    }

    private class SummaryMessageRenderer extends MessageRenderer {

        public void render(final Object obj, final TableBodyGroup rowGroup) {
            final GMMessage message = (GMMessage)obj;

            // render the main row
            super.render(message, rowGroup);

            // TODO: render the summary view
            int maxCellCount = 3;
            for (Iterator iter = rowGroup.getRows().iterator(); iter.hasNext();) {
                final TableRow row = (TableRow)iter.next();
                int cellCount = 0;
                for (Iterator cIter = row.getCells().iterator(); cIter.hasNext();) {
                    final TableCell cell = (TableCell)cIter.next();
                    cellCount += cell.getColSpan();
                }
                maxCellCount = Math.max(maxCellCount, cellCount);
            }

            final TableRow summaryRow = rowGroup.newTableRow();

            final TableDataCell skipCell = summaryRow.newTableDataCell();
            skipCell.addStyleName("gm-MessageList-td-Summary");
            skipCell.add(new HTML("&nbsp;"));
            summaryRow.add(skipCell);

            final TableDataCell detailCell = summaryRow.newTableDataCell();
            detailCell.addStyleName("gm-MessageList-td-Summary");
            detailCell.add(new HTML("&nbsp;")); // TODO: convert to detail triangle
            summaryRow.add(detailCell);

            final TableDataCell summaryCell = summaryRow.newTableDataCell();
            summaryCell.addStyleName("gm-MessageList-td-Summary");

            final int colSpan = maxCellCount - 2;
            if (colSpan > 1) {
                summaryCell.setColSpan(colSpan);
            }
            summaryCell.add(new MessageSummaryView(message));
            summaryRow.add(summaryCell);

            rowGroup.add(summaryRow);

            if (message.getSummary() == null) {
                final MessageServiceAsync service = MessageService.App.getInstance();
                service.fetchSummary(client.getAccount(), message, new AsyncCallback() {
                    public void onSuccess(final Object result) {
                        message.setSummary((GMMessageSummary)result);
                    }

                    public void onFailure(final Throwable caught) {
                        GWT.log("Problem fetching summary", caught);
                    }
                });
            }
        }
    }

    private static native void setAttribute(Element elem, String attr, String value) /*-{
        elem.setAttribute(attr,value);
    }-*/;

    private class AllFilterCommand implements Command {
        public void execute() {
            DeferredCommand.add(new Command() {
                public void execute() {
                    messagesFiltered.setFilter(null);
                }
            });
        }
    }

    private class HasFlagFilterCommand implements Command {
        private final GMFlags.GMFlag flag;

        public HasFlagFilterCommand(final GMFlags.GMFlag flag) {
            this.flag = flag;
        }

        public final void execute() {
            DeferredCommand.add(new Command() {
                public void execute() {
                    messagesFiltered.setFilter(new FilteredEventList.Filter() {
                        public boolean accept(final Object element) {
                            final GMMessage message = (GMMessage)element;
                            if (message.getFlags() != null) {
                                return acceptFlags(message.getFlags());
                            } else {
                                // TODO: fetch Flag info
                                return true;
                            }
                        }
                    });
                }
            });
        }

        protected boolean acceptFlags(final GMFlags flags) {
            return flags.contains(flag);
        }
    }

    private class NotFlagFilterCommand extends MessageList.HasFlagFilterCommand {
        public NotFlagFilterCommand(final GMFlags.GMFlag flag) {
            super(flag);
        }

        protected boolean acceptFlags(final GMFlags flags) {
            return !super.acceptFlags(flags);
        }
    }

    private class ToMeFilterCommand implements Command {
        public void execute() {
            DeferredCommand.add(new Command() {
                public void execute() {
                    messagesFiltered.setFilter(new FilteredEventList.Filter() {
                        public boolean accept(final Object element) {
                            final GMMessage message = (GMMessage)element;
                            final GMMessageHeaders messageHeaders = message.getHeaders();
                            if (messageHeaders != null) {
                                return checkHeaders(messageHeaders);
                            } else {
                                // TODO: fetch headers
                                // keep in list for now.
                                return true;
                            }
                        }
                    });
                }
            });
        }

        /**
         * Return true when the message is To: me.
         */
        protected boolean checkHeaders(final GMMessageHeaders messageHeaders) {
            final GMAddress[] tos = messageHeaders.getTo();
            if (tos != null) {
                for (int i=0; i < tos.length; i++) {
                    if (client.isMe(tos[i])) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private class ToCcMeFilterCommand extends ToMeFilterCommand {
        /**
         * Return true when the message is To: me or CC: me.
         */
        protected boolean checkHeaders(final GMMessageHeaders messageHeaders) {
            boolean toMe = super.checkHeaders(messageHeaders);
            if (!toMe) {
                final GMAddress[] ccs = messageHeaders.getCc();
                if (ccs != null) {
                    for (int i=0; i < ccs.length; i++) {
                        if (client.isMe(ccs[i])) {
                            toMe = true;
                            break;
                        }
                    }
                }
            }
            return toMe;
        }
    }

    private class NotToCcMeFilterCommand extends ToCcMeFilterCommand {
        /**
         * Return true when the message is not To: me or not CC: me.
         */
        protected boolean checkHeaders(final GMMessageHeaders messageHeaders) {
            return !super.checkHeaders(messageHeaders);
        }
    }

    private static class NotImplementedCommand implements Command {
        public void execute() {
            GWT.log("Feature not implemented yet.", new UnsupportedOperationException("Feature not implemented yet."));
            Window.alert("Feature not implemented yet.");
        }
    }

    private static class NotImplementedClickListener implements ClickListener {
        public void onClick(final Widget sender) {
            NOT_IMPLEMENTED_COMMAND.execute();
        }
    }

    private class SelectedMessagesListEventLisener implements ListEventListener {
        public SelectedMessagesListEventLisener() {
            messagesView.addListEventListener(this);
        }

        public void listChanged(final ListEvent listEvent) {
            // XXX: I'm not sure I need to check listEvent.isChanged()
            if (listEvent.isRemoved() || listEvent.isChanged()) {
                selectedMessages.retainAll(messagesView);
            }
        }
    }
}
