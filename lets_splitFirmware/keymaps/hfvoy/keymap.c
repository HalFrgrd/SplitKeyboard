#include "rev2.h"
#include "action_layer.h"
#include "eeconfig.h"
#include "timer.h"


extern keymap_config_t keymap_config;

// Each layer gets a name for readability, which is then used in the keymap matrix below.
// The underscores don't mean anything - you can have a layer called STUFF or any other name.
// Layer names don't all need to be of the same length, obviously, and you can also skip them
// entirely and just use numbers.
#define QWERTYBASE 0 // default layer
#define CUSTOMSHIFT 1 
#define MOUSE 2 
#define CLOSEWINDOW 3

// Fillers to make layering more clear
#define _______ KC_TRNS
#define XXXXXXX KC_NO
#define CT_ALB 2

const uint16_t PROGMEM keymaps[][MATRIX_ROWS][MATRIX_COLS] = {

/* QWERTYBASE
 * ,------------------------------------------------------------------------------------------------------------------------------.
 * | Esc  | blkn |   f1 |   f2 |   f3 |  f4  |  f5  |  f6  |   no |  f7  |  f8  |   f9 | f10  |  f11 | f12  | voldn| volup| mute  |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * | blkn | grv  |   1  |    2 |    3 |   4  |   5  |   6  |   no |   7  |   8  |    9 |   0  |  -   |   =  | no   | bksp | del   |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * | blkn | tab  |   q  |    w |    e |   r  |   t  |   no |   no |   y  |   u  |    i |   o  |  p   |   [  |  ]   |   \  | prtsc |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * | blkn | cpslk|   a  |    s |    d |   f  |   g  |   no |   no |   h  |   j  |    k |   l  |  ;   |   '  | no   | enter| no    |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * | blkn |lshift|   z  |    x |    c |   v  |   b  |   no |   no |   n  |   m  |    , |   .  |  /   |   no |rshift| upar | blkn  |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * | lctr | gui  |  alt | larr |   no |  spc |  no  |   no |   no | uparr| dwnar| rarr |  fn  |  no  | meta | larr | dwnar| rarr  |
 * `------------------------------------------------------------------------------------------------------------------------------'
 */
/* 
[QWERTYBASE] = KEYMAP ( \
  KC_Q,         KC_2,       KC_3,         KC_4,     KC_5,         KC_MINS, KC_EQL,    KC_6,        KC_6,        KC_6,        KC_6,         KC_6,         KC_6,           KC_6,          KC_7,        KC_8,         KC_9,           KC_0,     \
  KC_Q,         KC_W,       KC_E,         KC_R,     KC_T,         KC_TAB,  KC_TAB,    KC_Y,        KC_6,        KC_6,        KC_6,         KC_6,         KC_6,           KC_6,          KC_U,        KC_I,         KC_O,           KC_P,    \
  KC_Q,         KC_Q,       KC_Q,         KC_Q,     KC_Q,         KC_Q,    KC_Q,      KC_Q,        KC_6,        KC_6,        KC_6,         KC_6,         KC_6,           KC_6,          KC_Q,        KC_Q,         KC_Q,           KC_Q,     \
  KC_Z,         KC_X,       KC_C,         KC_V,     KC_B,         KC_SPC,  KC_ENT,    KC_N,        KC_6,        KC_6,        KC_6,         KC_6,           KC_6,         KC_6,          KC_M,        KC_COMM,      KC_DOT,         KC_QUOTE, \
  KC_Z,         KC_X,       KC_C,         KC_V,     KC_B,         KC_SPC,  KC_ENT,    KC_N,        KC_6,        KC_6,        KC_6,         KC_6,           KC_6,         KC_M,          KC_6,         KC_COMM,      KC_DOT,         KC_QUOTE, \
  KC_Z,         KC_X,       KC_C,         KC_V,     KC_B,         KC_SPC,  KC_ENT,    KC_N,        KC_6,        KC_6,        KC_6,         KC_6,           KC_6,         KC_M,       KC_COMM,         KC_6,         KC_DOT,         KC_QUOTE 
), */
 
[QWERTYBASE] = KEYMAP ( \
  KC_ESC , M(8)   ,   KC_F1,   KC_F2,   KC_F3,   KC_F4,   KC_F5,  KC_F6 ,XXXXXXX ,  KC_F7 ,   KC_F8,   KC_F9,  KC_F10, KC_F11 , KC_F12 ,  KC_VOLD, KC_VOLU,KC_AUDIO_MUTE, \
  M(7)   ,  KC_GRV,   KC_1 ,   KC_2 ,   KC_3 ,   KC_4 ,   KC_5 ,  KC_6  ,XXXXXXX ,  KC_7  ,   KC_8 ,   KC_9 ,   KC_0 , KC_MINS, KC_EQL , XXXXXXX , KC_BSPC, KC_DEL,        \
  M(5)   ,  KC_TAB,   KC_Q ,   KC_W ,   KC_E ,   KC_R ,   KC_T ,XXXXXXX ,XXXXXXX ,  KC_Y  ,   KC_U ,   KC_I ,   KC_O , KC_P   , KC_LBRC, KC_RBRC , KC_BSLS, KC_PSCR,       \
  M(3),LT(2,KC_CAPS),   KC_A ,   KC_S ,   KC_D ,   KC_F ,   KC_G ,XXXXXXX ,XXXXXXX ,  KC_H  ,   KC_J ,   KC_K ,   KC_L , KC_SCLN, KC_QUOT, XXXXXXX , KC_ENT , XXXXXXX,       \
  M(10)  , M(11)   ,   KC_Z ,   KC_X ,   KC_C ,   KC_V ,   KC_B ,XXXXXXX ,XXXXXXX ,  KC_N  ,   KC_M , KC_COMMA,  KC_DOT, KC_SLSH, XXXXXXX, M(12)    , KC_UP  , _______,       \
  KC_LCTL, KC_LGUI, KC_LALT, KC_LEFT, XXXXXXX,  KC_SPC, XXXXXXX,XXXXXXX ,XXXXXXX , KC_UP  , KC_DOWN,KC_RIGHT, KC_RCTL, XXXXXXX, KC_RGUI, KC_LEFT , KC_DOWN, KC_RIGHT      \
),



/* CUSTOMSHIFT
 * ,------------------------------------------------------------------------------------------------------------------------------.
 * |      | 	 |   	|      |      |      |      |  	   |  no  |      |      |      |      |      |      |      |      |       |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * |      | 	 |      |      |      |      |      |      |  no  |      |      |      |      |      |      | no   |      |       |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * |  	  | 	 |      |      |      |      |      |   no |  no  |      |      |      |      |      |      |      |      |       |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * |      |      |      |      |      |      |      |   no |  no  |      |      |      |      |      |      | no   |      |  no   |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * | 	  |      |      |      |      |      |      |   no |  no  |      |      |      |      |      |  no  |      |      |       |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * |      | 	 |  	| home |   no |  	 |  no  |   no |  no  | pgup | pgdwn| end  |  	  |  no  |      |      |      |       |
 * `------------------------------------------------------------------------------------------------------------------------------'
 */

[CUSTOMSHIFT] = KEYMAP ( \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  _______ ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , _______ , _______ , _______, _______, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  _______ ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , _______ , XXXXXXX , KC_DEL , _______, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  XXXXXXX ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , _______ , _______ , _______, _______, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  XXXXXXX ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , _______ , XXXXXXX , _______, XXXXXXX, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  XXXXXXX ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , XXXXXXX , _______ , _______, _______, \
  _______,  _______,   _______,   KC_HOME,   XXXXXXX ,   _______ ,   XXXXXXX ,  XXXXXXX ,  XXXXXXX , KC_PGUP   ,   KC_PGDN ,   KC_END  ,   _______ , XXXXXXX , _______ , _______ , _______, _______ \
),




/* MOUSE
 * ,------------------------------------------------------------------------------------------------------------------------------.
 * |      | 	 |   	|      |      |      |      |  	   |   no |      |      |      |      |      |      |      |      |       |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * |      | 	 |      |      |      |      |      |      |   no |      |      |      |      |      |      |  no  |      |       |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * |  	  | 	 |      |      |      |      |      |   no |   no |      |scrup |curup |      |      |      |      |      |       |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * |      |      | lClik|mClik |rClik |      |      |   no |   no |scrdwn|curlft|curdwn|curght|      |      |  no  |      |  no   |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * | 	  |      |      |      |      |      |      |   no |   no |      |      |      |      |      |  no  |      |      |       |
 * |------+------+------+------+------+-------------+------+------+------+------+------+------+------+------+------+------+-------|
 * |      | 	 |  	|      |   no |  	 |  no  |   no |   no |      |      |      |      |  no  |      |      |      |       |
 * `------------------------------------------------------------------------------------------------------------------------------'
 */

[MOUSE] = KEYMAP ( \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  _______ ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , _______ , _______ , _______, _______, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  _______ ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , _______ , XXXXXXX , _______, _______, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  XXXXXXX ,  XXXXXXX ,  _______  ,   KC_WH_U ,   KC_MS_U ,   _______ , _______ , _______ , _______ , _______, _______, \
  _______,  _______,   KC_BTN1,   KC_BTN3,   KC_BTN2 ,   _______ ,   _______ ,  XXXXXXX ,  XXXXXXX ,  KC_WH_D  ,   KC_MS_L ,   KC_MS_D ,   KC_MS_R , _______ , _______ , XXXXXXX , _______, XXXXXXX, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  XXXXXXX ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , XXXXXXX , _______ , _______, _______, \
  _______,  _______,   _______,   _______,   XXXXXXX ,   _______ ,   XXXXXXX ,  XXXXXXX ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , XXXXXXX , _______ , _______ , _______, _______ \
),


[CLOSEWINDOW] = KEYMAP ( \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  _______ ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , _______ , _______ , _______, _______, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  _______ ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , _______ , XXXXXXX , _______, _______, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  XXXXXXX ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , _______ , _______ , _______, _______, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  XXXXXXX ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , _______ , XXXXXXX , _______, XXXXXXX, \
  _______,  _______,   _______,   _______,   _______ ,   _______ ,   _______ ,  XXXXXXX ,  XXXXXXX ,  _______  ,   _______ ,   _______ ,   _______ , _______ , XXXXXXX , _______ , _______, _______, \
  _______,  _______,   _______,   _______,   XXXXXXX ,   _______ ,   XXXXXXX ,  XXXXXXX ,  XXXXXXX ,  _______  ,   _______ ,   M(9)    ,   _______ , XXXXXXX , _______ , _______ , _______, _______ \
),

};

const uint16_t PROGMEM fn_actions[] = {

};


const macro_t *action_get_macro(keyrecord_t *record, uint8_t id, uint8_t opt) 
{
  // This will hold the timestamp of when we start holding the key
  static uint16_t start;
  switch(id) {
    case 0: // M(0) access shift layer with left
        if (record->event.pressed) {
            //register_code(KC_LSFT);
            layer_on(1);
        } else {
            layer_off(1);
            //unregister_code(KC_LSFT);
        };
	case 1: // M(1) access shift layer with right
        if (record->event.pressed) {
            //register_code(KC_RSFT);
            layer_on(1);
        } else {
            layer_off(1);
            //unregister_code(KC_RSFT);
        };
	case 10: // M(10) copy and paste on press and release
        if (record->event.pressed) {
            return MACRO( D(LCTL), T(C), U(LCTL), END  );
        } else {
			return MACRO( D(LCTL), T(V), U(LCTL), END  );
        };
	
	case 9: // M(9)
        return MACRO(D(LALT),D(F4),U(F4),U(LALT), END);
	case 11:
		  // On keydown, start the timer
		  if (record->event.pressed) {
			  start = timer_read();
			  // Start layer 1
			  layer_on(1);
			  
		  } else {
			  layer_off(1); //  turn off layer 1
			  // On key up
			  // If time was greater than 150ms, it was a hold
			  if (timer_elapsed(start) > 105) {
				  // nothing happens because layer 1 takes care of it
				  
			  } else {
				  // Otherwise it was a tap, put in a open parentheses 
				  // There is no shift being held because layer 1 turned it off.
				  // So this is self contained shift.
				  
				  return MACRO(D(LSFT),T(9),U(LSFT),END);
			  }
		}
		unregister_mods(MOD_LSFT);
		unregister_code(KC_LSFT);
		
	case 12: // this is for right shift. layer 1 sees no difference between left or right.
		  // On keydown, start the timer
		  if (record->event.pressed) {
			  start = timer_read();
			  // Start layer 1
			  layer_on(1);
			  
		  } else {
			  layer_off(1); //  turn off layer 1
			  // On key up
			  // If time was greater than 150ms, it was a hold
			  if (timer_elapsed(start) > 105) {
				  // nothing happens because layer 1 takes care of it
				  
			  } else {
				  // Otherwise it was a tap, put in a open parentheses 
				  // There is no shift being held because layer 1 turned it off.
				  // So this is self contained shift.
				  
				  return MACRO(D(RSFT),T(0),U(RSFT),END);
			  }
		}
		unregister_mods(MOD_RSFT);
		unregister_code(KC_RSFT);
	



		break; 			
  }
  unregister_mods(MOD_LSFT);
  unregister_code(KC_LSFT);
  return MACRO_NONE;

};          


//Tap Dance Definitions
qk_tap_dance_action_t tap_dance_actions[] = {
  ///[0]  = ACTION_TAP_DANCE_FN(KC_RALT,OSL(CLOSEWINDOW)) // 1 tap: right alt, 2 taps: closes window
// Other declarations would go here, separated by commas, if you have them
}; 

bool process_record_user(uint16_t kc, keyrecord_t *rec) { // this function thingy allows to have custom shift layer. The shift mod is not used on the specified key
    uint8_t layer;
    layer = biton32(layer_state);  // get the current layer
    if (layer == CUSTOMSHIFT) {        // if it is your shift layer, then...
		
         if (kc >= KC_A && kc <= KC_EXSEL && !(  
         // if your keycode is in this range, shift will be enabled, unless you exclude it below
         // in my example, you don't want shift if 1 or 2 is pressed while on this layer
         // just add whichever keycodes you want to exclude
                 kc == KC_HOME ||
                 kc == KC_PGUP ||
				 kc == KC_PGDN ||
				 kc == KC_END  ||
				 kc == KC_DEL
            )) {
              if (rec->event.pressed) {
				register_mods(MOD_LSFT); //using macros is different to using mods for some reason.
				//MACRO(U(LSFT));
				//MACRO(D(LSFT));
              } else {
                unregister_mods(MOD_LSFT);
				//MACRO(U(LSFT));
              }
         }
    }
    return true;
}

/* // Use TD(CT_ALB) for the key in your keymap where you want this functionality

// Place these... anywhere, after your keymap

typedef struct {
  bool alt;
  bool finished_once;
} td_alb_state_t;

void _td_alb_finished (qk_tap_dance_state_t *state, void *user_data) {
  td_alb_state_t *s = (td_alb_state_t *)user_data;
  
  if (s->finished_once)
    return;
    
  s->finished_once = true;
  if (state->pressed) {
    s->alt = true;
    layer_on(1);
  } else {
    s->alt = false;
    register_code (KC_LSFT);
    register_code (KC_9);
  }
}

void _td_alb_reset (qk_tap_dance_state_t *state, void *user_data) {
  td_alb_state_t *s = (td_alb_state_t *)user_data;

  if (s->alt) {
    layer_off(1);
  } else {
    unregister_code (KC_LSFT);
    unregister_code (KC_9);
  }
  
  s->finished_once = false;
}

qk_tap_dance_action_t tap_dance_actions[] = {
  [CT_ALB]  = {
    .fn = { NULL, _td_alb_finished, _td_alb_reset },
    .user_data = (void *)&((td_alb_state_t) { false, false })
  }
}; */
