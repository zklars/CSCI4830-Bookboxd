<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<!--
  Material Design Lite
  Copyright 2015 Google Inc. All rights reserved.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License
-->
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="description"
	content="A front-end template that helps you build fast, modern mobile web apps.">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, minimum-scale=1.0">
<title>Bookboxd - ${userList.list_name} by ${userListOwner.username}</title>

<!-- Add to homescreen for Chrome on Android -->
<meta name="mobile-web-app-capable" content="yes">
<link rel="icon" sizes="192x192" href="images/android-desktop.png">

<!-- Add to homescreen for Safari on iOS -->
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta name="apple-mobile-web-app-title" content="Material Design Lite">
<link rel="apple-touch-icon-precomposed" href="images/ios-desktop.png">

<!-- Tile icon for Win8 (144x144 + tile color) -->
<meta name="msapplication-TileImage"
	content="images/touch/ms-touch-icon-144x144-precomposed.png">
<meta name="msapplication-TileColor" content="#3372DF">

<link rel="shortcut icon" href="images/favicon.png">

<!-- SEO: If your mobile URL is different from the desktop URL, add a canonical link to the desktop page https://developers.google.com/webmasters/smartphone-sites/feature-phones -->
<!--
    <link rel="canonical" href="http://www.example.com/">
    -->

<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Roboto:regular,bold,italic,thin,light,bolditalic,black,medium&amp;lang=en">
<link rel="stylesheet" href="https://fonts.googleapis.com/icon?family=Material+Icons">
<link rel="stylesheet" href="https://code.getmdl.io/1.3.0/material.cyan-light_blue.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
<link rel="stylesheet" href="dashboard.css">
<style>
#view-source {
	position: fixed;
	display: block;
	right: 0;
	bottom: 0;
	margin-right: 40px;
	margin-bottom: 40px;
	z-index: 900;
}
</style>
</head>
<body>
	<div
		class="demo-layout mdl-layout mdl-js-layout mdl-layout--fixed-drawer mdl-layout--fixed-header">
		<header
			class="demo-header mdl-layout__header mdl-color--grey-100 mdl-color-text--grey-600">
			<div class="mdl-layout__header-row">
				<span class="mdl-layout-title">List Viewer</span>
				<div class="mdl-layout-spacer"></div>
				<div
					class="mdl-textfield mdl-js-textfield mdl-textfield--expandable">
					<form action="Search" method="GET">
					<label class="mdl-button mdl-js-button mdl-button--icon"
						for="searchBox"> <i class="material-icons">search</i>
					</label>
					<div class="mdl-textfield__expandable-holder">
						<input class="mdl-textfield__input" type="text" id="searchBox" name="query">
						<label class="mdl-textfield__label" for="searchBox">Enter your query...</label>
						<input type="submit" style="display: none" />
					</div>
					</form>
				</div>
			</div>
		</header>
		<div
			class="demo-drawer mdl-layout__drawer mdl-color--blue-grey-900 mdl-color-text--blue-grey-50">
			<header class="demo-drawer-header">
				<div class="demo-avatar-dropdown">
					<span>Welcome, ${user.username}</span>
				</div>
			</header>
			<nav class="demo-navigation mdl-navigation mdl-color--blue-grey-800">
				<a class="mdl-navigation__link" href="ViewProfile?user_id=${user.user_id}">
					<i class="mdl-color-text--blue-grey-400 material-icons" role="presentation">account_circle</i>Profile</a>
				<a class="mdl-navigation__link" href="FriendsList?user_id=${user.user_id}">
					<i class="mdl-color-text--blue-grey-400 material-icons" role="presentation">people</i>Friends</a>
				<a class="mdl-navigation__link" href="FriendRequests">
					<i class="mdl-color-text--blue-grey-400 material-icons" role="presentation">person_add</i>Friend Requests</a>
				<c:if test="${userListOwner.user_id == user.user_id}">
					<c:if test="${isNotDefaultList}">
						<a class="mdl-navigation__link" href="ListEditor?list_id=${userList.list_id}">
							<i class="mdl-color-text--blue-grey-400 material-icons" role="presentation">edit</i>Edit List</a>
						<a class="mdl-navigation__link" href="Lists?action=deleteList&list_id=${userList.list_id}">
							<i class="mdl-color-text--blue-grey-400 material-icons" role="presentation">delete</i>Delete List</a>
					</c:if>
				</c:if>
				<a class="mdl-navigation__link" href="Search?query=">
					<i class="mdl-color-text--blue-grey-400 material-icons" role="presentation">book</i>Browse Books</a>
				<a class="mdl-navigation__link" href="Logout">
					<i class="mdl-color-text--blue-grey-400 material-icons" role="presentation">logout</i>Logout</a>
				<div class="mdl-layout-spacer"></div>
				<a class="mdl-navigation__link"
					href="https://github.com/zklars/CSCI4830-Bookboxd"><i
					class="mdl-color-text--blue-grey-400 material-icons"
					role="presentation">info</i><span class="visuallyhidden">GitHub</span></a>
			</nav>
		</div>
		<main class="mdl-layout__content mdl-color--grey-100">
		<div class="mdl-grid demo-content">
			<div class="demo-charts mdl-cell mdl-cell--12-col mdl-grid">
				<h3>${userList.list_name} by <a href="ViewProfile?user_id=${userListOwner.user_id}">${userListOwner.username}</a></h3>
				<br><br>
			</div>
			<c:forEach items="${userListBooks}" var="item">
				<!-- Card begin -->
				<div
					class="mdl-cell mdl-cell--4-col mdl-cell--4-col-tablet mdl-cell--4-col-phone mdl-card mdl-shadow--3dp">
					<div class="mdl-card__media">
						<img src="${item.image_url}" style="width: 100%;">
					</div>
					<div class="mdl-card__title">
						<h4 class="mdl-card__title-text">${item.book_name}</h4>
					</div>
					<div class="mdl-card__title">
						<h5><span class="fa fa-star checked"></span> &nbsp; ${item.average_rating}</h5>
					</div>
					<div class="mdl-card__supporting-text">
						<span class="mdl-typography--subhead">Genre: ${item.genre}</span>
					</div>
					<div class="mdl-card__supporting-text mdl-typography--subhead">
						<span class="mdl-typography--subhead">${item.description}</span>
					</div>
					<div class="mdl-card__actions">
						<ul class="mdl-menu mdl-js-menu mdl-menu--top-left mdl-js-ripple-effect"
							for="add-book${item.book_id}-to-list">
							<c:if test="${userListOwner.user_id == user.user_id}">
								<a class="mdl-menu__item mdl-menu__item--full-bleed-divider" href="Lists?action=remove&list_id=${userList.list_id}&book_id=${item.book_id}">Remove from List</a>						
							</c:if>
							<c:forEach items="${currentUserLists}" var="list">
								<a class="mdl-menu__item"
									href="Lists?action=add&list_id=${list.list_id}&user_id=${list.user_id}&book_id=${item.book_id}">${list.list_name}</a>
							</c:forEach>
						</ul>
						<a class="android-link mdl-button mdl-js-button android-link-menu mdl-typography--text-uppercase"
							id="add-book${item.book_id}-to-list">Actions<i class="material-icons">chevron_right</i></a>
					</div>
					<div></div>
				</div>
				<!-- Card end -->
			</c:forEach>
		</div>
		</main>
	</div>
	<script src="https://code.getmdl.io/1.3.0/material.min.js"></script>
</body>
</html>
