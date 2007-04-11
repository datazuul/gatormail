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

package edu.ufl.osg.gatormail.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import edu.ufl.osg.gatormail.client.model.Account;
import edu.ufl.osg.gatormail.client.model.GMAddress;
import edu.ufl.osg.gatormail.client.model.GMFolder;
import edu.ufl.osg.gatormail.client.model.GMInternetAddress;
import edu.ufl.osg.gatormail.client.model.message.GMMessage;
import edu.ufl.osg.gatormail.client.services.FoldersService;
import edu.ufl.osg.gatormail.client.services.FoldersServiceAsync;
import edu.ufl.osg.gatormail.client.services.LoginService;
import edu.ufl.osg.gatormail.client.services.LoginServiceAsync;
import edu.ufl.osg.gatormail.client.ui.HeaderPanel;
import edu.ufl.osg.gatormail.client.ui.NavPanel;
import edu.ufl.osg.gatormail.client.ui.message.MessageView;
import edu.ufl.osg.gatormail.client.ui.messageList.MessageList;
import org.mcarthur.sandy.gwt.event.property.client.PropertyChangeSource;
import org.mcarthur.sandy.gwt.login.client.LoginPanel;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * Widget for the mail client.
 *
 * @author Sandy McArthur
 */
public final class GatorMailWidget extends Composite implements HistoryListener, PropertyChangeSource {

    private final transient PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final SimplePanel sp = new SimplePanel();
    private final HorizontalPanel hp = new HorizontalPanel();
    private final HeaderPanel header = new HeaderPanel(this);

    private final LoginListener loginListener = new LoginListener();
    private final LoginPanel login = new LoginPanel(loginListener);

    private TabPanel tabs = new TabPanel();

    private Account account;

    public GatorMailWidget() {
        sp.addStyleName("gm-GatorMailWidget");
        sp.setWidth("100%");

        hp.addStyleName("gm-GatorMailWidget-hp");
        hp.setWidth("100%");
        hp.setVerticalAlignment(HasAlignment.ALIGN_TOP);

        initWidget(sp);

        sp.add(login);
        login.focus();

        final LoginServiceAsync loginService = LoginService.App.getInstance();
        loginService.autoLogin(new AsyncCallback() {
            public void onSuccess(final Object result) {
                if (result != null) {
                    final Account account = (Account)result;
                    setAccount(account);
                    login(account.getUsername(), account.getPassword());
                }
            }
            public void onFailure(final Throwable caught) {
                GWT.log("Auto login failed", caught);
            }
        });


        History.addHistoryListener(this);

        tabs.getTabBar().addStyleName("gm-MailClient-TabBar");
        tabs.getDeckPanel().addStyleName("gm-MailClient-DeckPanel");
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(final Account account) {
        Object old = this.account;
        this.account = account;
        pcs.firePropertyChange("account", old, account);
    }

    public boolean isMe(final GMAddress address) {
        if (address instanceof GMInternetAddress) {
            final GMInternetAddress internetAddress = (GMInternetAddress)address;
            final String to = internetAddress.getAddress();
            return getAccount().getEmailAddress().equals(to);
        } else if (address != null) {
            return getAccount().equals(address.getString());
        }
        return false;
    }

    public void updateInfo(final GMFolder folder) {
        final FoldersServiceAsync service = FoldersService.App.getInstance();
        service.updateFolder(account, folder, new AsyncCallback() {
            public void onSuccess(final Object result) {
                final GMFolder f = (GMFolder)result;
                folder.applyUpdate(f);
            }
            public void onFailure(final Throwable caught) {
                GWT.log("Problem updating info for: " + folder, caught);
            }
        });
    }

    private final VerticalPanel mainPanel = new VerticalPanel();

    private void loadMainView() {

        final NavPanel navPanel = new NavPanel(this);
        hp.add(navPanel);
        hp.setCellWidth(navPanel, "130px");

        mainPanel.addStyleName("gm-GatorMailWidget-mainPanel");
        mainPanel.setWidth("100%");
        mainPanel.add(header);
        mainPanel.add(tabs);
        
        hp.add(mainPanel);

        sp.setWidget(hp);
    }

    private void login(final String username, final String password) {
        final LoginServiceAsync loginService = LoginService.App.getInstance();
        loginService.doLogin(username, password, new AsyncCallback() {
            public void onSuccess(final Object result) {
                final LoginService.LoginResult loginResult = (LoginService.LoginResult) result;
                if (loginResult.isSuccess()) {
                    setAccount(loginResult.getAccount());
                    //header.setUsername(getAccount().getUsername());
                    loadMainView();
                    loadWelcome();
                }
            }

            public void onFailure(final Throwable caught) {
                login.setErrorMessage(caught.getMessage());
                login.reenable();
            }
        });
    }

    private void loadWelcome() {

        final VerticalPanel vp = new VerticalPanel();
        vp.setWidth("16.6ex");

        tabs.add(new HTML("This is alpha quality code.<br/>Expect it to be buggy and slow.<br/>Select on a folder on the left."), "Welcome");
        tabs.selectTab(0);

    }

    private Map openFolders = new HashMap();

    public void openFolder(final GMFolder gmFolder) {
        MessageList messageList = (MessageList)openFolders.get(gmFolder.getFullName());
        if (messageList != null) {
            tabs.selectTab(tabs.getWidgetIndex(messageList));
        } else {
            messageList = new MessageList(this, gmFolder);
            openFolders.put(gmFolder.getFullName(), messageList);
            tabs.add(messageList, gmFolder.getName());
            tabs.selectTab(tabs.getWidgetIndex(messageList));
        }
        History.newItem(gmFolder.getFullName());
    }

    private Map openMessages = new HashMap();
    public void openMessage(final GMMessage message) {
        MessageView mvp = (MessageView)openMessages.get(message);
        if (mvp != null) {
            tabs.selectTab(tabs.getWidgetIndex(mvp));
        } else {
            mvp = new MessageView(this, message);
            openMessages.put(message, mvp);
            final String subject = message.getHeaders().getSubject();
            final String tabText;
            if (subject != null && subject.length() > 0) {
                tabText = subject.substring(0, Math.min(25, subject.length()));                
            } else {
                tabText = "Message " + message.getUid();
            }
            tabs.add(mvp, tabText); // TODO: make this smarter
            tabs.selectTab(tabs.getWidgetIndex(mvp));
        }
    }

    /**
     * Fired when the user clicks the browser's 'back' or 'forward' buttons.
     *
     * @param historyToken the token representing the current history state
     */
    public void onHistoryChanged(final String historyToken) {
        final MessageList messageList = (MessageList)openFolders.get(historyToken);
        if (messageList != null) {
            tabs.selectTab(tabs.getWidgetIndex(messageList));
        }
    }

    public void addPropertyChangeListener(final PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(final PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    private class LoginListener implements LoginPanel.LoginListener {
        public void onSubmit(final LoginPanel loginPanel) {
            final String name = loginPanel.getUsername();
            final String password = loginPanel.getPassword();
            login(name, password);
        }
    }
}
