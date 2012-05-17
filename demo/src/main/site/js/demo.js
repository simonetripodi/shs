
/*
 * Copyright (c) 2012 Simone Tripodi (simonetripodi@apache.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
// shared utilities

/**
 * render the warning message
 */
function printInfo( message )
{
    printMessage( 'INFO   ', message );
}

/**
 * render the error message
 */
function printError( message )
{
    printMessage( 'ERROR  ', message );
}

/**
 * render the success message
 */
function printSuccess( message )
{
    printMessage( 'SUCCESS', message );
}

/**
 * render a message
 */
function printMessage( level, message )
{
    var now = new Date();
    var time = now.getHours()
               + ':'
               + now.getMinutes()
               + ':'
               + now.getSeconds();
    $( '#console' ).append( time + ' [' + level + '] ' + message + '\n' );
}

// init

$( document ).ready( function()
{
    $( '#img1' ).draggable( { iframeFix: true } );
    $( '#img2' ).draggable( { iframeFix: true } );
    $( '#img3' ).draggable( { iframeFix: true } );
    $( '#img4' ).draggable( { iframeFix: true } );

    $( '#droppable-frame' ).load( function()
    {
        $( '#droppable-frame' ).contents().find( 'pre' ).droppable(
        {
            drop: function( event, ui )
            {
                var now = new Date();
                var curr_date = now.getDate();
                var curr_month = now.getMonth() + 1;
                var curr_year = now.getFullYear();

                $.ajax(
                {
                    type: 'POST',
                    url: '/data/' + curr_year
                                  + '/'
                                  + curr_month
                                  + '/'
                                  + curr_date
                                  + '.txt',
                    contentType: 'text/plain',
                    data: curr_year
                                  + ':'
                                  + curr_month
                                  + ':'
                                  + curr_date
                                  + ' - '
                                  + ui.draggable.attr( 'id' )
                                  + ';\n',
                    global: false,
                    beforeSend: function()
                    {
                        printInfo( 'Request started...' );
                    },
                    complete: function()
                    {
                        printInfo( 'Request complete.' );
                    },
                    success: function( data )
                    {
                        printSuccess( 'Server replied <code>'
                                      + data.status
                                      + ': '
                                      + data.responseText
                                      + '</code>' );
                    },
                    error: function( data )
                    {
                        printError( 'Server replied <code>'
                                    + data.status
                                    + ': '
                                    + data.responseText
                                    + '</code>' );
                    }
                } );
            }
        } );
    } );
});
