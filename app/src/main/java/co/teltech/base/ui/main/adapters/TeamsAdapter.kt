package co.teltech.base.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import co.teltech.base.R
import co.teltech.base.shared.kotlin.setCircleGradientColor
import co.teltech.base.vo.Team


class TeamsAdapter(private val context: Context, private var teamList: List<Team>) : RecyclerView.Adapter<TeamsAdapter.TeamsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.item_recycler_team_info, parent, false)
        return TeamsViewHolder(view)
    }

    override fun onBindViewHolder(holder: TeamsViewHolder, position: Int) {
        holder.bindTeam(teamList[position])
    }

    override fun getItemCount(): Int {
        return teamList.size
    }

    inner class TeamsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val teamBubble: ImageView = itemView.findViewById(R.id.teamBubbleColor)
        private val teamName: TextView = itemView.findViewById(R.id.teamText)
        fun bindTeam(team: Team){
            teamName.text = team.teamName
            teamBubble.setCircleGradientColor(team.teamColor, team.teamColor)
        }
    }
}