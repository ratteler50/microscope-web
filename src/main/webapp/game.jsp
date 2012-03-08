<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<% // Json Here %>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Microscope</title>
    <link href="css/game.css" rel="stylesheet" type="text/css">
    <script type="text/javascript" src="scripts/jquery-1.8.2.min.js"></script>
    <script type="text/javascript" src="scripts/game.js"></script>
</head>
<body>
<div id='header'>
    <div id='headerInfo'>
        <div id='leftHeader'>
            <div id='bigPicture'>
                <b>Big Picture:</b>
                <span></span>
            </div>
            <div id='playerLabel'>
                <b>Player:</b>
                <span></span>
            </div>
            <div id='lens'>
                <b>Lens:</b>
                <span></span>
            </div>
            <div id='focus'>
                <b>Focus:</b>
                <span></span>
            </div>
            <div id='legacyButton' style='display: none;'>
                Pick Legacy
            </div>
            <div id='focusButton' style='display: none;'>
                Choose Focus
            </div>
        </div>
        <div id='centerHeader'>
            <div id='legacyText' style='display: none;'>
                <b>Legacy:</b>
                <span></span>
            </div>
        </div>
        <div id='rightHeader'>
            <div id='yes_palette'>
                <div id='yes_button'>Do</div>
                <ul id='yes_list' class='list'></ul>
            </div>
            <div id='no_palette'>
                <div id='no_button'>Don't</div>
                <ul id='no_list' class='list'></ul>
            </div>
        </div>
    </div>
</div>
<script type='noscript' id='periodTemplate'>
	<div id='temp_container' class='period_container'>
		<div id='temp_leftPlusContainer' class='period_plus_container period_plus_left' style='display : none;'>
			<div class='period_plus'>
				+
			</div>
		</div>
		<div id='temp' class='period'>
			<textarea class='period_textbox' />
			<div class='period_text' style='display : none;'></div>
			<div class='period_tone'></div>
			<div class='period_done'>
				Done
			</div>
			<div class='period_cancel'>
				Cancel
			</div>
		</div>
		<div id='temp_rightPlusContainer' class='period_plus_container period_plus_right' style='display : none;'>
			<div class='period_plus'>
				+
			</div>
		</div>
		<div id='temp_childPlusContainer' class='period_plus_event_container' style='display : none;'>
			<div class='period_plus_event'>
				+
			</div>
		</div>
	</div>
</script>
<script type='noscript' id='eventTemplate'>
	<div id='temp_container' class='event_container'>
		<div id='temp_topPlusContainer' class='event_plus_container event_plus_top' style='display : none;'>
			<div class='event_plus'>
				+
			</div>
		</div>
		<div id='temp' class='event'>
			<textarea class='event_textbox' />
			<div class='event_text' style='display : none;'></div>
			<div class='event_tone'></div>
			<div class='event_done'>
				Done
			</div>
			<div class='event_cancel'>
				Cancel
			</div>
		</div>
		<div id='temp_bottomPlusContainer' class='event_plus_container event_plus_bottom' style='display : none;'>
			<div class='event_plus'>

			</div>
		</div>
		<div id='temp_childPlusContainer' class='event_plus_scene_container' style='display : none;'>
			<div class='event_plus_scene'>+</div>
		</div>
	</div>
</script>
<script type='noscript' id='sceneTemplate'>
	<div id='temp_container' class='scene_container'>
		<div id='temp_leftPlusContainer' class='scene_plus_container scene_plus_left' style='display : none;'>
			<div class='scene_plus'>+</div>
		</div>
		<div id='temp' class='scene'>
			<textarea class='scene_textbox' />
			<div class='scene_text' style='display : none;'>
			</div>
			<hr class='scene_line' style='top: 45px;' />
			<textarea class='scene_textbox' style='top: 60px;' />
			<div class='scene_text' style='top: 55px; display : none;'>
			</div>
			<hr class='scene_line' style='top: 100px;' />
			<textarea class='scene_textbox' style='top: 115px; display : none;' />
			<div class='scene_text' style='top: 110px; display : none;'>
			</div>
			<div class='scene_tone' style='display : none;'></div>
			<div class='scene_done'>
				Next
			</div>
			<div class='scene_cancel'>
				Cancel
			</div>
		</div>
		<div id='temp_rightPlusContainer' class='scene_plus_container scene_plus_right' style='display : none;'>
			<div class='scene_plus'>+</div>
		</div>
	</div>
</script>
<script type='noscript' id='sceneContainerTemplate'>
<div id='temp_scenesContainer' class='scenes_container'>
</div>

</script>
<script type='noscript' id='scenePlaceholderTemplate'>
<div id='temp_placeholder' class='scenes_placeholder'>
	<div class='scenes_placeholder_number'>
		0
	</div>
</div>
</script>
<div id='game'>
</div>
<div id='lightbox'></div>
<div id='scenesHideScrollbar'></div>
<div id='legacyContainer'>
    <div id='oldLegacy'>
        <div id='oldLegacyText'></div>
        <div id='oldLegacyKeep'>Keep</div>
    </div>
    <div id='newLegacy'>
        <div id='newLegacyText'></div>
        <textarea id='newLegacyTextbox'></textarea>
        <div id='newLegacyDone'>Done</div>
    </div>
</div>
<div id='createFocus' style='display: none;'>
    <textarea id='createFocusTextbox'></textarea>
    <div id='createFocusDone'>Done</div>
</div>
<div id='createBigPicture' style='display:none'>
    <div id='createBigPictureTitle'>Big Picture</div>
    <textarea id='createBigPictureText'></textarea>
    <div id='createBigPictureDone'>Done</div>
</div>
<div id='palette' style='display:none'>
    <div id='paletteTitle'>Palette</div>
    <textarea id='paletteText'></textarea>
    <div id='paletteYes'>Suggest</div>
    <div id='paletteNo'>Ban</div>
    <div id='paletteSkip'>Skip</div>
</div>
</body>
</html>