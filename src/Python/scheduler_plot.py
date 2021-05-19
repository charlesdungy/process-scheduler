# -----------------------------------------------------------------------------
# creates plot based on job scheduling data
# -----------------------------------------------------------------------------

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns

class SchedulerPlot:
    def __init__(self):
        self.jobs = None

    def read_data(self):
        """  """
        self.jobs = pd.read_csv('../../data/processed/jobs.txt', 
                                sep='\t', 
                                header=None
                                )
        self.jobs.columns = ['Process', 'Arrival', 'Service', 
                            'Start_FCFS', 'Start_SPN', 'Start_HRRN'
                            ]
        return self.jobs

    def create_plot(self, process, begin, service, title):
        """ """
        begin = list(begin)
        process = list(process)
        service = list(service)

        begin.reverse()
        process.reverse()
        service.reverse()
        
        sns.set_theme(
            context='paper', 
            style='whitegrid', 
            palette='deep', 
            font='Helvetica', 
            rc={'axes.titlesize': 16,
                'axes.titlepad': 10,
                'xtick.labelsize': 8,
                'ytick.labelsize': 8,
                'ytick.major.pad': 3,
                'xtick.major.pad': 3}
        )

        _, ax = plt.subplots(figsize=(8, 3))
        yval = np.arange(0.3, 0.3 * (len(process) + 1), 0.3)
        total_runtime = sum(service)
        
        ax.barh(
            y=yval, 
            left=begin, 
            width=service, 
            height=0.29
        )

        ax.set(
            xlim=(0, total_runtime), 
            xticks=np.arange(0, total_runtime + 1, 2), 
            title=title,
            yticks=yval,
            yticklabels=process
        )
        
        plt.suptitle(
            'Length of Time Each Job Executes', 
            y=0.9, 
            fontsize=8
        )

        ax.grid(
            b=True, 
            color='grey', 
            linestyle='-.', 
            linewidth=0.5, 
            alpha=0.4
        )

        sns.despine(bottom=True)
        file_name = title.replace(' ', '_').lower()

        plt.savefig(
            '../../plots/{}.png'.format(file_name), 
            dpi=250, 
            facecolor='w', 
            edgecolor='w', 
            orientation='portrait', 
            format=None, 
            transparent=False, 
            bbox_inches=None, 
            pad_inches=0.1, 
            metadata=None
        )

sp = SchedulerPlot()
df = sp.read_data()
sp.create_plot(
    df.Process, 
    df.Start_FCFS, 
    df.Service, 
    'First Come First Serve'
)

sp.create_plot(
    df.Process, 
    df.Start_SPN, 
    df.Service, 
    'Shortest Process Next'
)

sp.create_plot(
    df.Process, 
    df.Start_HRRN, 
    df.Service, 
    'Highest Response Ratio Next'
)