/*
 * TransformDirection.java
 *
 * Copyright (C) Roger Millington 2006-2010
 *
 *-----------------------------------------------------------------------
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 *----------------------------------------------------------------------
 */
package fft;

/**
 * Enumeration used to indicate which direction a transformation should
 * take.
 * <p>
 * Updated in 2010 from a simple 'type safe enumeration' class so that
 * the transforms no longer supports JDK earlier than 1.5.
 * 
 * @author Roger Millington
 */
public enum TransformDirection
{
    Forward, Inverse;
}
