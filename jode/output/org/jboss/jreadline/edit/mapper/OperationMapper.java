/* OperationMapper - Decompiled by JODE
 * Visit http://jode.sourceforge.net/
 */
package org.jboss.jreadline.edit.mapper;
import org.jboss.jreadline.edit.actions.Operation;

public class OperationMapper
{
    public static Operation mapToFunction(String function) {
	if (!function.equals("abort")) {
	    if (!function.equals("accept-line")) {
		if (!function.equals("backward-char")) {
		    if (!function.equals("backward-delete-char")) {
			if (!function.equals("backward-kill-line")) {
			    if (!function.equals("backward-kill-word")) {
				if (!function.equals("backward-word")) {
				    if (!function
					     .equals("beginning-of-history")) {
					if (!function.equals
					     ("beginning-of-line")) {
					    if (!function.equals
						 ("call-last-kbd-macro")) {
						if (!function.equals
						     ("capitalize-word")) {
						    if (!function.equals
							 ("character-search")) {
							if (!function.equals
							     ("character-search-backward")) {
							    if (!function
								     .equals
								 ("clear-screen")) {
								if (!function
									 .equals
								     ("complete")) {
								    if (!function
									     .equals
									 ("copy-backward-word")) {
									if (!function.equals
									     ("copy-forward-word")) {
									    if (!function.equals("delete-char")) {
										if (!function.equals("delete-char-or-list")) {
										    if (!function.equals("delete-horizontal-space")) {
											if (!function.equals("digit-argument")) {
											    if (!function.equals("do-uppercase-version")) {
												if (!function.equals("downcase-word")) {
												    if (!function.equals("dump-functions")) {
													if (!function.equals("dump-macros")) {
													    if (!function.equals("dump-variables")) {
														if (!function.equals("emacs-editing-mode")) {
														    if (!function.equals("end-kbd-macro")) {
															if (!function.equals("end-of-history")) {
															    if (!function.equals("end-of-line")) {
																if (!function.equals("exchange-point-and-mark")) {
																    if (!function.equals("forward-backward-delete-char")) {
																	if (!function.equals("forward-char")) {
																	    if (!function.equals("forward-search-history")) {
																		if (!function.equals("forward-word")) {
																		    if (!function.equals("history-search-backward")) {
																			if (!function.equals("history-search-forward")) {
																			    if (!function.equals("insert-comment")) {
																				if (!function.equals("insert-comletions")) {
																				    if (!function.equals("kill-line")) {
																					if (!function.equals("kill-region")) {
																					    if (!function.equals("kill-whole-line")) {
																						if (!function.equals("kill-word")) {
																						    if (!function.equals("menu-complete")) {
																							if (!function.equals("menu-complete-backward")) {
																							    if (!function.equals("next-history")) {
																								if (!function.equals("non-incremental-forward-search-history")) {
																								    if (!function.equals("non-incremental-reverse-search-history")) {
																									if (!function.equals("overwrite-mode")) {
																									    if (!function.equals("possible-completions")) {
																										if (!function.equals("prefix-meta")) {
																										    if (!function.equals("previous-history")) {
																											if (!function.equals("quoted-insert")) {
																											    if (!function.equals("re-read-init-file")) {
																												if (!function.equals("redraw-current-line")) {
																												    if (!function.equals("reverse-search-history")) {
																													if (!function.equals("revert-line")) {
																													    if (!function.equals("self-insert")) {
																														if (!function.equals("set-mark")) {
																														    if (!function.equals("skip-csi-sequence")) {
																															if (!function.equals("start-kbd-macro")) {
																															    if (!function.equals("tab-insert")) {
																																if (!function.equals("tilde-expand")) {
																																    if (!function.equals("tilde-expand")) {
																																	if (!function.equals("transpose-chars")) {
																																	    if (!function.equals("transpose-words")) {
																																		if (!function.equals("undo")) {
																																		    if (!function.equals("universal-argument")) {
																																			if (!function.equals("unix-filename-rubout")) {
																																			    if (!function.equals("unix-line-discard")) {
																																				if (!function.equals("unix-word-rubout")) {
																																				    if (!function.equals("upcase-word")) {
																																					if (!function.equals("vi-editing-mode")) {
																																					    if (!function.equals("yank")) {
																																						if (!function.equals("yank-last-arg")) {
																																						    if (!function.equals("yank-nth-arg")) {
																																							if (!function.equals("yank-pop"))
																																							    return Operation.NO_ACTION;
																																							return Operation.NO_ACTION;
																																						    }
																																						    return Operation.NO_ACTION;
																																						}
																																						return Operation.NO_ACTION;
																																					    }
																																					    return Operation.PASTE_AFTER;
																																					}
																																					return Operation.VI_EDIT_MODE;
																																				    }
																																				    return Operation.NO_ACTION;
																																				}
																																				return Operation.DELETE_PREV_BIG_WORD;
																																			    }
																																			    return Operation.DELETE_BEGINNING;
																																			}
																																			return Operation.NO_ACTION;
																																		    }
																																		    return Operation.NO_ACTION;
																																		}
																																		return Operation.UNDO;
																																	    }
																																	    return Operation.NO_ACTION;
																																	}
																																	return Operation.NO_ACTION;
																																    }
																																    return Operation.NO_ACTION;
																																}
																																return Operation.NO_ACTION;
																															    }
																															    return Operation.NO_ACTION;
																															}
																															return Operation.NO_ACTION;
																														    }
																														    return Operation.NO_ACTION;
																														}
																														return Operation.NO_ACTION;
																													    }
																													    return Operation.NO_ACTION;
																													}
																													return Operation.NO_ACTION;
																												    }
																												    return Operation.SEARCH_PREV;
																												}
																												return Operation.NO_ACTION;
																											    }
																											    return Operation.NO_ACTION;
																											}
																											return Operation.NO_ACTION;
																										    }
																										    return Operation.HISTORY_PREV;
																										}
																										return Operation.NO_ACTION;
																									    }
																									    return Operation.NO_ACTION;
																									}
																									return Operation.NO_ACTION;
																								    }
																								    return Operation.NO_ACTION;
																								}
																								return Operation.NO_ACTION;
																							    }
																							    return Operation.HISTORY_NEXT;
																							}
																							return Operation.NO_ACTION;
																						    }
																						    return Operation.NO_ACTION;
																						}
																						return Operation.DELETE_NEXT_WORD;
																					    }
																					    return Operation.DELETE_ALL;
																					}
																					return Operation.NO_ACTION;
																				    }
																				    return Operation.DELETE_END;
																				}
																				return Operation.NO_ACTION;
																			    }
																			    return Operation.NO_ACTION;
																			}
																			return Operation.NO_ACTION;
																		    }
																		    return Operation.NO_ACTION;
																		}
																		return Operation.MOVE_NEXT_WORD;
																	    }
																	    return Operation.SEARCH_NEXT;
																	}
																	return Operation.MOVE_NEXT_CHAR;
																    }
																    return Operation.DELETE_NEXT_CHAR;
																}
																return Operation.NO_ACTION;
															    }
															    return Operation.MOVE_END;
															}
															return Operation.NO_ACTION;
														    }
														    return Operation.NO_ACTION;
														}
														return Operation.EMACS_EDIT_MODE;
													    }
													    return Operation.NO_ACTION;
													}
													return Operation.NO_ACTION;
												    }
												    return Operation.NO_ACTION;
												}
												return Operation.CASE;
											    }
											    return Operation.NO_ACTION;
											}
											return Operation.NO_ACTION;
										    }
										    return Operation.NO_ACTION;
										}
										return Operation.NO_ACTION;
									    }
									    return Operation.DELETE_NEXT_CHAR;
									}
									return Operation.YANK_NEXT_WORD;
								    }
								    return (Operation
									    .YANK_PREV_WORD);
								}
								return (Operation
									.COMPLETE);
							    }
							    return (Operation
								    .CLEAR);
							}
							return (Operation
								.NO_ACTION);
						    }
						    return Operation.NO_ACTION;
						}
						return Operation.CASE;
					    }
					    return Operation.NO_ACTION;
					}
					return Operation.MOVE_BEGINNING;
				    }
				    return Operation.HISTORY_NEXT;
				}
				return Operation.MOVE_PREV_WORD;
			    }
			    return Operation.DELETE_PREV_WORD;
			}
			return Operation.DELETE_BEGINNING;
		    }
		    return Operation.DELETE_PREV_CHAR;
		}
		return Operation.MOVE_PREV_CHAR;
	    }
	    return Operation.NEW_LINE;
	}
	return Operation.ABORT;
    }
}
