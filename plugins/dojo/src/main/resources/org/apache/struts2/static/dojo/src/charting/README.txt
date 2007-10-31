Dojo Charting Engine
=========================================================================
The Dojo Charting Engine is a (fairly) complex object structure, designed
to provide as much flexibility as possible in terms of chart construction.
To this end, the engine details the following structure:

Chart
---PlotArea[]
------Plot[]
---------Axis (axisX)
---------Axis (axisY)
---------Series[]


A Chart object is the main entity; it is the entire graphic.  A Chart may
have any number of PlotArea objects, which are the basic canvas against 
which data is plotted.  A PlotArea may have any number of Plot objects,
which is a container representing up to 2 axes and any number of series
to be plotted against those axes; a Series represents a binding against
two fields from a data source (initial rev, this data source is always of
type dojo.collections.Store but this will probably change once dojo.data
is in production).

The point of this structure is to allow for as much flexibility as possible
in terms of what kinds of charts can be represented by the engine.  The
current plan is to accomodate up to analytical financial charts, which tend
to have 3 plot areas and any number of different types of axes on each one.

The main exception to this is the pie chart, which will have it's own
custom codebase.  Also, 3D charts are not accounted for at this time,
although the only thing that will probably need to be altered to make
that work would be Plot and Series (to accomodate the additional Z axis).

Finally, a Plot will render its series[] through the use of Plotters, which
are custom methods to render specific types of charts.
-------------------------------------------------------------------------
In terms of widgets, the basic concept is that there is a central, super-
flexible Chart widget (Chart, oddly enough), and then any number of preset
chart type widgets, that are basically built to serve a simple, easy 
purpose.  For instance, if someone just needs to plot a series of lines,
they would be better off using the LineChart widget; but if someone needed
to plot a combo chart, that has 2 Y Axes (one linear, one log) against the
same X Axis, using lines and areas, then they will want to use a Chart widget.
Note also that unlike other widgets, the Charting engine *can* be called
directly from script *without* the need for the actual widget engine to be
loaded; the Chart widgets are thin wrappers around the charting engine.